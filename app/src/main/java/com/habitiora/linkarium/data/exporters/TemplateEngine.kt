package com.habitiora.linkarium.data.exporters

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateEngine @Inject constructor() {
    companion object {
        val tokenRegex: Regex
            get() = "@([a-zA-Z0-9_]+)".toRegex()
        val listRegex: Regex
            get() = "#([a-zA-Z0-9_]+)".toRegex()
    }

    fun process(template: String, values: Map<String, String>): String {
        var result = template
        // Reemplaza cada ocurrencia del token por su valor correspondiente
        result = tokenRegex.replace(result) { matchResult ->
            val key = matchResult.groupValues[1]
            values[key] ?: matchResult.value // Si no existe la key, deja el token original
        }
        return result
    }
}