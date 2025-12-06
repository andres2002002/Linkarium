package com.habitiora.linkarium.ui.utils.pubsAndSubs

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenSelectionManager @Inject constructor() {
    private val _selectedGardenIndex = MutableStateFlow(0)
    val selectedGardenIndex = _selectedGardenIndex.asStateFlow()

    fun selectGarden(index: Int) {
        if (index < 0 || index == selectedGardenIndex.value) return
        _selectedGardenIndex.value = index
        Timber.d("Jardín seleccionado: $index")
    }
}