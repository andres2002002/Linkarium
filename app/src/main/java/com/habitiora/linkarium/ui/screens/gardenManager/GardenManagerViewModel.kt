package com.habitiora.linkarium.ui.screens.gardenManager

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GardenManagerViewModel @Inject constructor(
    private val gardenRepository: LinkGardenRepository
) : ViewModel() {

    private val _nameTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val nameTextFieldValue: StateFlow<TextFieldValue> = _nameTextFieldValue.asStateFlow()
    fun setNameTextFieldValue(value: TextFieldValue) {
        _nameTextFieldValue.value = value
    }

    private val _descriptionTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val descriptionTextFieldValue: StateFlow<TextFieldValue> = _descriptionTextFieldValue.asStateFlow()
    fun setDescriptionTextFieldValue(value: TextFieldValue) {
        _descriptionTextFieldValue.value = value
    }

    val isValidGarden: StateFlow<Boolean> = combine(
        nameTextFieldValue,
        descriptionTextFieldValue
    ) { name, description ->
        name.text.isNotBlank() && name.text.length >= 3
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun saveGarden(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                if (!isValidGarden.value) return@launch
                val garden = LinkGardenEntity(
                    name = nameTextFieldValue.value.text,
                    description = descriptionTextFieldValue.value.text
                )
                val gardenId = gardenRepository.insert(garden)
                Timber.d("Garden saved with id: $gardenId")
                onSuccess()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}