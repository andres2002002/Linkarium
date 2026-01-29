package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportStatus
import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    fun export(request: ExportRequest): Flow<ExportStatus>
}
