package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.core.SnackbarMessage
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A singleton event bus specifically designed for handling Snackbar notifications across the application.
 *
 * This class uses a [SharedFlow] to broadcast [SnackbarMessage] events from producers (typically ViewModels)
 * to a centralized consumer (usually an Activity or a top-level Composable) that displays the Snackbar.
 * It decouples the UI components triggering the message from the UI component displaying it.
 *
 * Usage:
 * - **Producers:** Inject this class and call [postMessage] to queue a new Snackbar.
 * - **Consumers:** Collect the [events] flow to react to incoming messages.
 */
@Singleton
class SnackbarEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<SnackbarMessage>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events: SharedFlow<SnackbarMessage> = _events.asSharedFlow()

    // Función para enviar un mensaje desde cualquier ViewModel
    suspend fun postMessage(message: SnackbarMessage) {
        _events.emit(message)
    }
}