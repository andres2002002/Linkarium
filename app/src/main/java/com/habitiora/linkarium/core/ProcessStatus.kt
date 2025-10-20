package com.habitiora.linkarium.core

sealed class ProcessStatus<out T> {
    // 1. Estado inicial o de espera.
    // No contiene datos ni errores, simplemente la operación aún no se ha iniciado
    // o está en cola. Es similar a un 'Idle'.
    object Waiting : ProcessStatus<Nothing>()

    // 2. Estado de carga/progreso.
    // Indica que la operación está en curso. Puedes añadir un Int para el progreso (0-100).
    object Loading : ProcessStatus<Nothing>()

    // 3. Estado de finalización satisfactoria.
    // Contiene el resultado exitoso (T, que podría ser una lista de archivos, una URL, etc.).
    // 'T' es el tipo de dato que obtienes al terminar.
    data class Success<out T>(val data: T) : ProcessStatus<T>()

    // 4. Estado de error.
    // Contiene información sobre el fallo. Podría ser un mensaje o una excepción.
    data class Error(val message: String) : ProcessStatus<Nothing>()

    object Empty : ProcessStatus<Nothing>()
}