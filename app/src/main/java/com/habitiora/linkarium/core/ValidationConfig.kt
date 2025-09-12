package com.habitiora.linkarium.core

/**
 * Configuración base para las validaciones.
 * Permite personalizar aspectos comunes de las validaciones.
 */
sealed class ValidationConfig {
    data class TextConfig(
        val allowEmpty: Boolean = true,
        val minLength: Int = 0,
        val maxLength: Int = Int.MAX_VALUE,
        val pattern: Regex? = null
    ) : ValidationConfig()
}