package com.habitiora.linkarium.data.exporters

import android.content.Context
import android.net.Uri
import com.google.gson.GsonBuilder
import com.habitiora.linkarium.core.UriTypeAdapter
import com.habitiora.linkarium.core.exporters.ExportContent
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.data.local.datasource.ExportDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.domain.model.Exporter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class JsonExporter @Inject constructor(
    private val gardenDataSource: LinkGardenDataSource,
    private val exportDataSource: ExportDataSource,
    @ApplicationContext private val context: Context
) : Exporter {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
        .setPrettyPrinting()
        .create()

    override fun canHandle(format: ExportFormat) = format is ExportFormat.Json

    override suspend fun export(request: ExportRequest) {
        val content = when (request.content) {
            is ExportContent.OnlyGardens -> gardenDataSource.getForList(request.content.gardenIds)
            is ExportContent.GardenAndSeeds -> exportDataSource.getGardensWithSeeds(request.content.gardenIds)
            ExportContent.FullBackup -> exportDataSource.getAllGardensWithSeeds()
        }

        val output = context.contentResolver.openOutputStream(request.uri) ?: throw Exception("Cannot open output stream")
        output.use { it.write(gson.toJson(content).toByteArray(Charsets.UTF_8)) }
    }
}