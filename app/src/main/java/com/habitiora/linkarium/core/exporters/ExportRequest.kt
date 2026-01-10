package com.habitiora.linkarium.core.exporters

import android.net.Uri

data class ExportRequest(
    val content: ExportContent,
    val format: ExportFormat,
    val uri: Uri
)