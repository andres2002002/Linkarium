package com.habitiora.linkarium.core.exporters

import android.net.Uri

sealed interface ExportState {
    data object Idle : ExportState
    data object Loading : ExportState
    data class Success(val uri: Uri) : ExportState
    data class Error(val throwable: Throwable) : ExportState
}