package com.habitiora.linkarium.domain.model

import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportRequest

interface Exporter {
    fun canHandle(format: ExportFormat): Boolean
    suspend fun export(request: ExportRequest)
}
