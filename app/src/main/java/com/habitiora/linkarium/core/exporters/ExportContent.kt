package com.habitiora.linkarium.core.exporters

/**
 * Represents the available types of content that can be exported.
 *
 * Each variant defines the scope of data included in the export process:
 * - [Gardens]: Exports gardens along with their contained seeds (links/items).
 * - [FullBackup]: Performs a complete export of all application data and configurations.
 */
sealed interface ExportContent {
    data class Gardens(val gardenIds: List<Long>): ExportContent
    data object FullBackup : ExportContent
}