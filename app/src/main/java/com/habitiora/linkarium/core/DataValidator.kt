package com.habitiora.linkarium.core

import android.util.Patterns
import com.habitiora.linkarium.R

/**
 * Utilidad principal para validación de datos con configuración flexible.
 */
object DataValidator {
    /**
     * Validación de nombre con configuración personalizable.
     */
    fun validateName(
        input: String?,
        config: ValidationConfig.TextConfig = ValidationConfig.TextConfig(
            allowEmpty = true,
            minLength = 3
        )
    ): ValidationResult = when {
        input.isNullOrEmpty() -> ValidationResult(config.allowEmpty)
        input.isBlank() -> ValidationResult(false, R.string.name_no_empty)
        input.length < config.minLength -> ValidationResult(false, R.string.name_characters)
        else -> ValidationResult(true)
    }

    /**
     * Validación de URL con configuración personalizable.
     */
    fun validateUrl(
        input: String?,
        config: ValidationConfig.TextConfig = ValidationConfig.TextConfig(
            allowEmpty = true
        )
    ): ValidationResult = when {
        input.isNullOrEmpty() -> ValidationResult(config.allowEmpty)
        !Patterns.WEB_URL.matcher(input).matches() -> ValidationResult(false, R.string.app_name)
        else -> ValidationResult(true)
    }

}