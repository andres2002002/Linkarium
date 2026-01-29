package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportStatus
import com.habitiora.linkarium.domain.model.Exporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepositoryImpl @Inject constructor(
    private val exporters: Set<@JvmSuppressWildcards Exporter>,
) : ExportRepository {
    override fun export(request: ExportRequest): Flow<ExportStatus> {
        val exporter = exporters.firstOrNull { it.canHandle(request.format) }
            ?: return emptyFlow() // Or emit Error

        return exporter.export(request)
    }
}
