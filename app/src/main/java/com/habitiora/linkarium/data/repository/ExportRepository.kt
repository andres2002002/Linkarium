package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.core.exporters.ExportRequest

interface ExportRepository {
    suspend fun export(request: ExportRequest)
}