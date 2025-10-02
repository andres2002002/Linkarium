package com.habitiora.linkarium.data.repository

import android.net.Uri
import com.google.gson.GsonBuilder
import com.habitiora.linkarium.core.GardenPdfGenerator
import com.habitiora.linkarium.core.UriTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepositoryImpl @Inject constructor(
    private val gardenRepository: LinkGardenRepository,
    private val pdfGenerator: GardenPdfGenerator
) : ExportRepository {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
        .setPrettyPrinting()
        .create()

    private suspend fun safeExport(action: suspend () -> Unit): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                action()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Export failed")
            Result.failure(e)
        }
    }

    override suspend fun exportGardensJson(output: OutputStream): Result<Unit> = safeExport {
        val gardens = gardenRepository.getAllWithSeeds()
        output.use { it.write(gson.toJson(gardens).toByteArray(Charsets.UTF_8)) }
    }

    override suspend fun exportGardensTxt(output: OutputStream): Result<Unit> {
        // Si el formato TXT es realmente igual al JSON, puedes reutilizar la función.
        // Si en el futuro cambia, ya tienes un método separado.
        return exportGardensJson(output)
    }

    override suspend fun exportGardensPdf(output: OutputStream): Result<Unit> = safeExport {
        val gardens = gardenRepository.getAllWithSeeds()
        output.use { pdfGenerator.generate(gardens, it) }
    }

    // --- Exportación de UN SOLO jardín ---

    override suspend fun exportGardenJson(gardenId: Long, output: OutputStream): Result<Unit> = safeExport {
        val garden = gardenRepository.getGardenWithSeeds(gardenId)
        output.use { it.write(gson.toJson(garden).toByteArray(Charsets.UTF_8)) }
    }

    override suspend fun exportGardenTxt(gardenId: Long, output: OutputStream): Result<Unit> {
        return exportGardenJson(gardenId, output)
    }

    override suspend fun exportGardenPdf(gardenId: Long, output: OutputStream): Result<Unit> = safeExport {
        val garden = gardenRepository.getGardenWithSeeds(gardenId)?: throw Exception("Garden not found")
        // Reutilizamos el generador, que puede manejar una lista de un solo elemento
        output.use { pdfGenerator.generate(listOf(garden), it) }
    }
}