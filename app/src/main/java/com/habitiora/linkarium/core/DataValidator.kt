package com.habitiora.linkarium.core

import android.net.Uri
import android.util.Patterns
import androidx.core.net.toUri
import com.habitiora.linkarium.R
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlin.text.trim

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
        input.trim().toUriOrNull() == null -> ValidationResult(false, R.string.app_name)
        else -> ValidationResult(true)
    }

    private fun String.toUriOrNull(): Uri? {
        return runCatching { toUri() }.getOrNull()
    }
}