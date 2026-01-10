package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.data.repository.ExportRepository
import javax.inject.Inject

class ExportUseCase @Inject constructor(
    private val repository: ExportRepository
) {
    suspend operator fun invoke(request: ExportRequest) {
        repository.export(request)
    }
}