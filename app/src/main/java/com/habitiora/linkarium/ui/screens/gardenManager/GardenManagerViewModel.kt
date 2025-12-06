package com.habitiora.linkarium.ui.screens.gardenManager

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenUpdateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GardenManagerViewModel @Inject constructor(
    private val gardenRepository: LinkGardenRepository,
    private val gardenUpdateManager: GardenUpdateManager
) : ViewModel() {

    // garden cache
    private val gardenUpdate = MutableStateFlow<LinkGarden?>(null)

    private val _nameTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val nameTextFieldValue: StateFlow<TextFieldValue> = _nameTextFieldValue.asStateFlow()

    private val _descriptionTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val descriptionTextFieldValue: StateFlow<TextFieldValue> = _descriptionTextFieldValue.asStateFlow()

    val isValidGarden: StateFlow<Boolean> = gardenUpdate.map{ garden ->
        garden != null && garden.name.isNotBlank() && garden.name.length >= 3
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun setupFieldsAndGarden(){
        viewModelScope.launch {
            // escuchar cambios en gardenUpdate ya que el dialogo esta en la pantalla principal
            gardenUpdateManager.gardenUpdate.collect { garden ->
                gardenUpdate.update { garden }
                // Actualizar los campos de texto con los datos del jardín si no es null
                garden?.let {
                    _nameTextFieldValue.update { it.copy(text = garden.name) }
                    _descriptionTextFieldValue.update { it.copy(text = garden.description) }
                }
            }
        }
    }

    init {
        setupFieldsAndGarden()
    }

    fun setNameTextFieldValue(value: TextFieldValue) {
        _nameTextFieldValue.update { value }
        gardenUpdate.update { it?.update(name = value.text) }
    }
    fun setDescriptionTextFieldValue(value: TextFieldValue) {
        _descriptionTextFieldValue.update { value }
        gardenUpdate.update { it?.update(description = value.text) }
    }

    fun saveGarden() {
        viewModelScope.launch {
            try {
                // varificamos validez del formulario
                if (!isValidGarden.value) return@launch
                // obtenemos el cache
                val garden = gardenUpdate.value ?: return@launch
                // si el id es 0 es nueva, sino es actualización
                if (garden.id != 0L) {
                    gardenRepository.update(garden)
                    Timber.d("Garden updated with id: ${garden.id}")
                } else {
                    val gardenId = gardenRepository.insert(garden)
                    Timber.d("Garden saved with id: $gardenId")
                }
                // cerramos el dialogo
                gardenUpdateManager.setGardenUpdate(null)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}