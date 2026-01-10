package com.habitiora.linkarium.ui.utils.pubsAndSubs

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bus para eventos de agregar una semilla y controlar el enabled del boton de agregar.
 */
@Singleton
class AddSeedEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    private val _enable = MutableStateFlow(false)
    val enable: StateFlow<Boolean> = _enable.asStateFlow()

    // Función para enviar un mensaje desde cualquier ViewModel
    fun emitEvent() {
        _events.tryEmit(Unit)
    }

    fun updateEnable(value: Boolean) {
        _enable.value = value
    }
}