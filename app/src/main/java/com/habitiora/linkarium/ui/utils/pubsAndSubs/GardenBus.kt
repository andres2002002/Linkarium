package com.habitiora.linkarium.ui.utils.pubsAndSubs

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenBus @Inject constructor() {
    private val _selectedGardenId = MutableStateFlow<Long>(1)
    val selectedGardenId = _selectedGardenId.asStateFlow()

    fun selectGarden(id: Long) {
        _selectedGardenId.value = id
        Timber.d("Jardín seleccionado: $id")
    }
}