package com.habitiora.linkarium.core.exporters

sealed interface ExportStatus {
    data object Idle : ExportStatus

    data class InProgress(
        val current: Int,
        val total: Int,
        val percentage: Float // Helper para barras de progreso (0.0 a 1.0)
    ) : ExportStatus

    data class Success(val outputUri: String? = null) : ExportStatus

    data class Error(val exception: Throwable) : ExportStatus
}