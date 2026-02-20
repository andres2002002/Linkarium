package com.habitiora.linkarium.core.exporters

sealed interface ExportFormat {
    val name: String

    /**
     * Extension used to save the file without the dot (e.g. "json", "pdf", "html")
     */
    val extension: String
    companion object{
        val allFormats = listOf(Backup, Json)
    }
    data object Json : ExportFormat{
        override val name: String
            get() = "JSON"
        override val extension: String
            get() = "json"
    }
    data object Backup : ExportFormat{
        override val name: String
            get() = "Backup"
        override val extension: String
            get() = "linkarium"
    }
}