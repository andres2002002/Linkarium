package com.habitiora.linkarium.core.exporters

sealed interface ExportFormat {
    data object Json : ExportFormat
    data object Pdf : ExportFormat
    data object Html : ExportFormat
}