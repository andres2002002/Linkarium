package com.habitiora.linkarium.domain.model

import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportStatus
import kotlinx.coroutines.flow.Flow

interface Exporter {
    fun canHandle(format: ExportFormat): Boolean
    fun export(request: ExportRequest): Flow<ExportStatus>
}
