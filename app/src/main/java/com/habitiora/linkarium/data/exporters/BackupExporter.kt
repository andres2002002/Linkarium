package com.habitiora.linkarium.data.exporters

import android.content.Context
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportStatus
import com.habitiora.linkarium.data.local.room.dao.LinkGardenReadDao
import com.habitiora.linkarium.domain.model.Exporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import java.util.zip.GZIPOutputStream
import javax.inject.Inject

class BackupExporter @Inject constructor(
    private val dao: LinkGardenReadDao,
    @ApplicationContext private val context: Context
) : Exporter {

    // Configuración compacta: sin espacios, sin pretty print
    private val json = Json {
        prettyPrint = false
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    override fun canHandle(format: ExportFormat): Boolean = format is ExportFormat.Backup
    override fun export(request: ExportRequest): Flow<ExportStatus> = flow {
        //Obtener IDs para iterar (poca memoria)
        val gardenIds = dao.getAllIds()
        val total = gardenIds.size

        emit(ExportStatus.InProgress(0, total, 0f))

        val outputStream = context.contentResolver.openOutputStream(request.uri)
            ?: throw Exception("Cannot open output stream for URI: ${request.uri}")
        // Encapsular el stream en GZIP -> Buffered -> Writer
        // El 'use' cierra todo automáticamente al final.
        GZIPOutputStream(outputStream).buffered().writer(Charsets.UTF_8).use { writer ->

            // Escribimos la estructura manual para simular un objeto JSON gigante
            // sin tenerlo todo en memoria.

            // Header del JSON
            val metadata = AppBackupMetadata()
            writer.write("{")
            writer.write("\"meta\":${json.encodeToString(metadata)},")
            writer.write("\"data\":[") // Inicio del array de jardines

            gardenIds.forEachIndexed { index, id ->
                // Carga LAZY de DB
                val aggregate = dao.getGardenById(id)

                if (aggregate != null) {
                    if (index > 0) writer.write(",")

                    // Serialización nativa rápida
                    val jsonString = json.encodeToString(aggregate)
                    writer.write(jsonString)
                }

                // Progreso
                val current = index + 1
                if (current % 5 == 0 || current == total) {
                    emit(ExportStatus.InProgress(current, total, current.toFloat() / total))
                }
            }

            writer.write("]") // Fin array data
            writer.write("}") // Fin objeto root
        }

        emit(ExportStatus.Success())
    }.flowOn(Dispatchers.IO)
}