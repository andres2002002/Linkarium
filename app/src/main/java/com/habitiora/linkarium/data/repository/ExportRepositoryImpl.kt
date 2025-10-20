package com.habitiora.linkarium.data.repository

import android.net.Uri
import androidx.room.withTransaction
import com.google.gson.GsonBuilder
import com.habitiora.linkarium.core.GardenPdfGenerator
import com.habitiora.linkarium.core.UriTypeAdapter
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSource
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.usecase.toDomain
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Async
import timber.log.Timber
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val pdfGenerator: GardenPdfGenerator,
    private val gardenDataSource: LinkGardenDataSource,
    private val seedDataSource: LinkSeedDataSource,
    private val entryDataSource: LinkEntryDataSource,
    private val tagDataSource: LinkTagDataSource
) : ExportRepository {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
        .setPrettyPrinting()
        .create()

    private suspend fun safeExport(action: suspend () -> Unit): Result<Unit> {
        return runCatching {
            withContext(Dispatchers.IO) { action() }
        }.onFailure { Timber.e(it, "Export failed") }
    }

    override suspend fun exportGardensJson(output: OutputStream): Result<Unit> = safeExport {
        val gardens = getAllGardensWithSeeds()
        output.use { it.write(gson.toJson(gardens).toByteArray(Charsets.UTF_8)) }
    }

    override suspend fun exportGardensTxt(output: OutputStream): Result<Unit> {
        // Si el formato TXT es realmente igual al JSON, puedes reutilizar la función.
        // Si en el futuro cambia, ya tienes un método separado.
        return exportGardensJson(output)
    }

    override suspend fun exportGardensPdf(output: OutputStream): Result<Unit> = safeExport {
        val gardens = getAllGardensWithSeeds()
        /*output.use { pdfGenerator.generate(gardens, it) }*/
    }

    // --- Exportación de UN SOLO jardín ---

    override suspend fun exportGardenJson(gardenId: Long, output: OutputStream): Result<Unit> = safeExport {
        val garden = getSeedsOf(gardenId)
        output.use { it.write(gson.toJson(garden).toByteArray(Charsets.UTF_8)) }
    }

    override suspend fun exportGardenTxt(gardenId: Long, output: OutputStream): Result<Unit> {
        return exportGardenJson(gardenId, output)
    }

    override suspend fun exportGardenPdf(gardenId: Long, output: OutputStream): Result<Unit> = safeExport {
        val garden = getSeedsOf(gardenId)
        // Reutilizamos el generador, que puede manejar una lista de un solo elemento
        /*output.use { pdfGenerator.generate(listOf(garden), it) }*/
    }

    private suspend fun getAllGardensWithSeeds(): Map<LinkGarden, List<LinkSeed>> =
        db.withTransaction {
            val gardens = gardenDataSource.getAll().first()
            gardens.associateWith { garden ->
                getSeedsOf(garden.id)
            }
        }

    private suspend fun getSeedsOf(gardenId: Long): List<LinkSeed> =
        seedDataSource.getSeedsForExport(gardenId)
            .map { getLinkSeedDomain(it) }

    private suspend fun getLinkSeedDomain(seed: LinkSeedEntity): LinkSeed {
        val entries = entryDataSource.getLinksBySeed(seed.id).first()
        val tags = tagDataSource.getTagsBySeed(seed.id).first()
        return seed.toDomain(entries, tags)
    }
}