package com.habitiora.linkarium.core

import androidx.compose.material3.SnackbarDuration

// Define el tipo de dato que quieres enviar
sealed class SnackbarMessage(
    open val message: String,
    open val actionLabel: String? = null,
    open val duration: SnackbarDuration = SnackbarDuration.Short
) {
    data class Info(override val message: String) : SnackbarMessage(message)
    data class Success(override val message: String) : SnackbarMessage(message)
    data class Error(override val message: String) : SnackbarMessage(message, duration = SnackbarDuration.Long)
}