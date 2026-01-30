package com.habitiora.linkarium.core.exporters

sealed interface ExportFormat {
    val name: String

    /**
     * Extension used to save the file without the dot (e.g. "json", "pdf", "html")
     */
    val extension: String
    companion object{
        val allFormats = listOf(Json, Pdf, Html)
    }
    data object Json : ExportFormat{
        override val name: String
            get() = "JSON"
        override val extension: String
            get() = "json"
    }
    data object Pdf : ExportFormat{
        override val name: String
            get() = "PDF"
        override val extension: String
            get() = "pdf"
    }
    data object Html : ExportFormat{
        override val name: String
            get() = "HTML"
        override val extension: String
            get() = "html"
    }
}