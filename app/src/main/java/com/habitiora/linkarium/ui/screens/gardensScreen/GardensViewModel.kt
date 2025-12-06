package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenUpdateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class GardensViewModel  @Inject constructor(
    private val gardenRepository: LinkGardenRepository,
    private val gardenUpdateManager: GardenUpdateManager
) : ViewModel() {
    private val _gardens = MutableStateFlow<List<LinkGarden>>(emptyList())
    val gardens: StateFlow<List<LinkGarden>> = _gardens.asStateFlow()

    private var isDragging = false
    private var willUpdateOrder = false
    private fun setupGardens(){
// 1. Cargar datos iniciales de Room
        viewModelScope.launch {
            gardenRepository.getAll().collect { dbGardens ->
                if (!isDragging) {
                    // Usamos update para asegurar consistencia
                    _gardens.update { dbGardens }
                }
            }
        }
    }

    init {
        setupGardens()
    }

    fun onDragStart() {
        isDragging = true
    }

    fun moveGarden(from: Int, to: Int) {
        if (from == to) return

        willUpdateOrder = true
        _gardens.update { current ->
            current.toMutableList().apply {
                add(to, removeAt(from))
            }
        }
    }

    fun onDragEnd() {
        isDragging = false

        // AHORA es el momento de guardar en Room
        saveOrderToRoom()
    }

    private fun saveOrderToRoom() {
        val currentList = _gardens.value
        viewModelScope.launch(Dispatchers.IO) {
            // Reasignamos el índice 'order' según la posición actual en la lista
            val updatedList = currentList.mapIndexed { index, garden ->
                garden.update(order = index + 1)
            }
            // Llamada a tu DAO
            gardenRepository.update(updatedList)
        }
    }

    fun onAddGarden(){
        gardenUpdateManager.setGardenUpdate(DatabaseContract.LinkGarden.Empty)
    }

    fun onEditGarden(garden: LinkGarden){
        gardenUpdateManager.setGardenUpdate(garden)
    }
}