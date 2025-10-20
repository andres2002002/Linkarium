package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.core.SnackbarMessage
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class SnackbarEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<SnackbarMessage>()
    val events: SharedFlow<SnackbarMessage> = _events.asSharedFlow()

    // Función para enviar un mensaje desde cualquier ViewModel
    suspend fun postMessage(message: SnackbarMessage) {
        _events.emit(message)
    }
}