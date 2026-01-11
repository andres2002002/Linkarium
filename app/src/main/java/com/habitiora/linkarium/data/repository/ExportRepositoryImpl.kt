package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.domain.model.Exporter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepositoryImpl @Inject constructor(
    private val exporters: Set<@JvmSuppressWildcards Exporter>,
) : ExportRepository {
    override suspend fun export(request: ExportRequest) {
        val exporter = exporters.firstOrNull { it.canHandle(request.format) }
            ?: error("No exporter for format ${request.format}")

        exporter.export(request)
    }
}