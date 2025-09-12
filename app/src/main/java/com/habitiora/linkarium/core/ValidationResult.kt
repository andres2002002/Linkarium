package com.habitiora.linkarium.core

/**
 * Resultado de la validación que incluye el estado y posible mensaje de error.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessageRes: Int? = null
)