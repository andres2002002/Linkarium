package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportStatus
import com.habitiora.linkarium.data.repository.ExportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExportUseCase @Inject constructor(
    private val repository: ExportRepository
) {
    operator fun invoke(request: ExportRequest): Flow<ExportStatus> {
        return repository.export(request)
    }
}
