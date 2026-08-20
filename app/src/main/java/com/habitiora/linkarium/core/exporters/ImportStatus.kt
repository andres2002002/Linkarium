package com.habitiora.linkarium.core.exporters

import android.net.Uri

sealed interface ImportStatus {
    data object Idle : ImportStatus

    data object InProgress : ImportStatus

    data class Success(val outputUri: Uri? = null) : ImportStatus

    data class Error(val exception: Throwable) : ImportStatus
}