package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.core.exporters.ExportRequest
import java.io.OutputStream

interface ExportRepository {
    suspend fun export(request: ExportRequest)
    suspend fun exportGardensJson(output: OutputStream): Result<Unit>
    suspend fun exportGardensTxt(output: OutputStream): Result<Unit>
    suspend fun exportGardensPdf(output: OutputStream): Result<Unit>

    suspend fun exportGardenJson(gardenId: Long, output: OutputStream): Result<Unit>
    suspend fun exportGardenTxt(gardenId: Long, output: OutputStream): Result<Unit>
    suspend fun exportGardenPdf(gardenId: Long, output: OutputStream): Result<Unit>
}