package com.habitiora.linkarium.data.exporters

import android.content.Context
import android.net.Uri
import com.google.gson.GsonBuilder
import com.habitiora.linkarium.core.UriTypeAdapter
import com.habitiora.linkarium.core.exporters.ExportContent
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.data.exporters.schema.LinkGardenAggregateSchema
import com.habitiora.linkarium.data.local.datasource.ExportDataSource
import com.habitiora.linkarium.domain.model.Exporter
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class JsonExporter @Inject constructor(
    private val exportDataSource: ExportDataSource,
    @ApplicationContext private val context: Context
) : Exporter {

    private val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
        .setPrettyPrinting()
        .create()

    override fun canHandle(format: ExportFormat) = format is ExportFormat.Json

    private val template = $$"""
        {
        	"garden:new_tag_garden": {
        		"custom_1": "nya_@id_@name",
        		"custom_2": "Nya_@description",
        		"order": "@order"
        	},
        	"seeds:new_tag_seeds": {
        		"$schema": {
        			"seed": {
        				"new_id": "@id",
        				"name": "@name",
        				"gardenId": "@gardenId",
        				"order": "@order",
        				"isFavorite": "@isFavorite",
        				"notes": "@notes",
        				"modifiedAt": "@modifiedAt"
        			},
        			"tags:new_tag_tags": {
        				"$schema": {}
        			},
        			"entries:new_tag_entries": {
        				"$schema": {
        					"id": "@id",
        					"seedId": "@seedId",
        					"order": "@order",
        					"uri": "@uri",
        					"label": "@label",
        					"note": "@note"
        				}
        			}
        		}
        	}
        }
    """.trimIndent()

    override suspend fun export(request: ExportRequest) {
        val content = when (request.content) {
            is ExportContent.Gardens -> exportDataSource.getGardensWithSeeds(request.content.gardenIds)
            ExportContent.FullBackup -> exportDataSource.getAllGardensWithSeeds()
        }
        content.firstOrNull()?.let {
            val jsonTest = LinkGardenAggregateSchema.schema.generateJsonFromTemplate(it, template)
            Timber.d(jsonTest)
        }
        val output = context.contentResolver.openOutputStream(request.uri) ?: throw Exception("Cannot open output stream")
        output.use { it.write(gson.toJson(content).toByteArray(Charsets.UTF_8)) }
    }
}