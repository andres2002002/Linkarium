package com.habitiora.linkarium.core.exporters

/**
 * Represents the available types of content that can be exported.
 *
 * Each variant defines the scope of data included in the export process:
 * - [OnlyGardens]: Exports only the garden metadata and structure.
 * - [GardenAndSeeds]: Exports gardens along with their contained seeds (links/items).
 * - [FullBackup]: Performs a complete export of all application data and configurations.
 */
sealed interface ExportContent {
    data class OnlyGardens(val gardenIds: List<Long>): ExportContent
    data class GardenAndSeeds(val gardenIds: List<Long>): ExportContent
    data object FullBackup : ExportContent
}