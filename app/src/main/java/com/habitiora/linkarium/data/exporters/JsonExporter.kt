package com.habitiora.linkarium.data.exporters

import android.content.Context
import com.habitiora.linkarium.core.exporters.ExportContent
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportNode
import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportSchema
import com.habitiora.linkarium.core.exporters.ExportStatus
import com.habitiora.linkarium.data.exporters.schema.LinkGardenAggregateSchema
import com.habitiora.linkarium.data.local.datasource.ExportDataSource
import com.habitiora.linkarium.domain.model.Exporter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedWriter
import java.io.OutputStream
import javax.inject.Inject

class JsonExporter @Inject constructor(
    private val exportDataSource: ExportDataSource,
    @ApplicationContext private val context: Context
) : Exporter {

    override fun canHandle(format: ExportFormat) = format is ExportFormat.Json

    private val defaultTemplate = """
{
    "garden:garden": {
        "id": "@id",
        "name": "@name",
        "description": "@description",
        "order": "@order"
    },
    "seeds:seeds": {
       "${'$'}schema": {
          "seed:seed": {
             "id": "@id",
             "name": "@name",
             "notes": "@notes",
             "order": "@order",
             "isFavorite": "@isFavorite"
          },
          "tags:tags": {
             "${'$'}schema": {
                 "tag": "@tag"
             }
          },
          "entries:entries": {
             "${'$'}schema": {
                "id": "@id",
                "label": "@label",
                "order": "@order",
                "uri": "@uri",
                "note": "@note"
             }
          }
       }
    }
}
    """.trimIndent()

    override fun export(request: ExportRequest): Flow<ExportStatus> = flow {
        val totalRecords = when (request.content) {
            is ExportContent.Gardens -> request.content.gardenIds.size
            ExportContent.FullBackup -> exportDataSource.countGardens()
        }

        if (totalRecords == 0) {
            emit(ExportStatus.Success())
            return@flow
        }

        val dataFlow = when (request.content) {
            is ExportContent.Gardens -> exportDataSource.getGardensWithSeedsFlow(request.content.gardenIds)
            ExportContent.FullBackup -> exportDataSource.getAllGardensWithSeedsFlow()
        }

        val outputStream = context.contentResolver.openOutputStream(request.uri)
            ?: throw Exception("Cannot open output stream for URI: ${request.uri}")

        emitAll(
            execute(
                outputStream = outputStream,
                dataFlow = dataFlow,
                schema = LinkGardenAggregateSchema.schema,
                rawTemplate = defaultTemplate,
                totalRecords = totalRecords
            )
        )
    }.flowOn(Dispatchers.IO)

    private fun <T> execute(
        outputStream: OutputStream,
        dataFlow: Flow<List<T>>,
        schema: ExportSchema<T>,
        rawTemplate: String,
        totalRecords: Int
    ): Flow<ExportStatus> = flow {

        emit(ExportStatus.InProgress(0, totalRecords, 0f))

        val writer = BufferedWriter(outputStream.writer())
        val templateElement = schema.prepareTemplate(rawTemplate)

        var processedCount = 0

        try {
            writer.write("[\n")
            var isFirstGlobal = true

            // We take the first emission because we expect a single list for now based on current DataSource
            val batch = dataFlow.first()

            batch.forEach { item ->
                if (!isFirstGlobal) {
                    writer.write(",\n")
                }

                val itemJson = schema.processEntity(item, templateElement)
                writer.write("  $itemJson")

                isFirstGlobal = false
                processedCount++

                if (processedCount % 5 == 0 || processedCount == totalRecords) {
                    emit(
                        ExportStatus.InProgress(
                            current = processedCount,
                            total = totalRecords,
                            percentage = processedCount.toFloat() / totalRecords.toFloat()
                        )
                    )
                }
            }

            writer.write("\n]")
            writer.flush()
            emit(ExportStatus.Success())

        } catch (e: Exception) {
            emit(ExportStatus.Error(e))
        } finally {
            writer.close()
            outputStream.close()
        }
    }
}
