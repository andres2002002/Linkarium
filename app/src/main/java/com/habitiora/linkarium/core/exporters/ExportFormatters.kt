package com.habitiora.linkarium.core.exporters

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ExportFormatters {

    val default: (Any?) -> String = { it?.toString().orEmpty() }

    val isoDateTime: (Any?) -> String = {
        (it as? LocalDateTime)?.format(DateTimeFormatter.ISO_DATE_TIME).orEmpty()
    }

    val boolean01: (Any?) -> String = {
        when (it as? Boolean) {
            true -> "1"
            false -> "0"
            else -> ""
        }
    }
}
