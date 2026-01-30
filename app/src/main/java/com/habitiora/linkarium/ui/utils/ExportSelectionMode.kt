package com.habitiora.linkarium.ui.utils

import com.habitiora.linkarium.core.exporters.ExportContent

enum class ExportSelectionMode {
    SelectedGardens,
    AllGardens;

    /**
     * Convierte el modo de selección a [ExportContent]
     */
    fun toExportContent(gardenIds: List<Long> = emptyList()): ExportContent =
        when (this) {
            SelectedGardens -> ExportContent.Gardens(gardenIds)
            AllGardens -> ExportContent.FullBackup
        }
}