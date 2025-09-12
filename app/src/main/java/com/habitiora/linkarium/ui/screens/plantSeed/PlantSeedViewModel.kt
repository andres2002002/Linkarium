package com.habitiora.linkarium.ui.screens.plantSeed

import android.net.Uri
import android.util.Patterns
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.domain.model.LinkGarden
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlantSeedViewModel @Inject constructor(
    private val seedRepository: LinkSeedRepository,
    private val gardenRepository: LinkGardenRepository
) : ViewModel() {

    val gardens: StateFlow<List<LinkGarden>> = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _collectionId = MutableStateFlow<Long>(0)
    val collectionId: StateFlow<Long> = _collectionId.asStateFlow()
    fun setCollectionId(id: Long) {
        _collectionId.value = id
    }
    private val _nameTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val nameTextFieldValue: StateFlow<TextFieldValue> = _nameTextFieldValue.asStateFlow()
    fun setNameTextFieldValue(value: TextFieldValue) {
        _nameTextFieldValue.value = value
    }

    private val _newUrlTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val newUrlTextFieldValue: StateFlow<TextFieldValue> = _newUrlTextFieldValue.asStateFlow()
    fun setNewUrlTextFieldValue(value: TextFieldValue) {
        _newUrlTextFieldValue.value = value
    }

    private fun isLikelyValidWebUri(uri: Uri): Boolean {
        val s = uri.toString()
        // chequeo simple con Patterns para evitar insertar basura
        return Patterns.WEB_URL.matcher(s).matches() || uri.scheme in listOf("content", "file")
    }

    private val _urlList = MutableStateFlow<List<Uri>>(emptyList())
    val urlList: StateFlow<List<Uri>> = _urlList.asStateFlow()
    /**
     * Intenta añadir una URL representada como String.
     * @return true si el string se convirtió en Uri y se añadió (o ya existía), false si el string no es una Uri válida.
     */
    fun addUrl(url: String): Boolean {
        val uri = runCatching { url.trim().toUri() }.getOrNull() ?: return false
        if (!isLikelyValidWebUri(uri)) return false
        addUri(uri)
        return true
    }

    /**
     * Añade un Uri a la lista. La operación evita duplicados de forma atómica.
     * No devuelve nada porque la operación se realiza atómicamente con `update`.
     */
    fun addUri(uri: Uri) {
        _urlList.update { current ->
            // LinkedHashSet preserva orden de inserción y evita duplicados
            LinkedHashSet(current).apply { add(uri) }.toList()
        }
    }

    fun removeUrl(url: String): Boolean {
        val uri = runCatching { url.trim().toUri() }.getOrNull() ?: return false
        removeUri(uri)
        return true
    }

    /**
     * Elimina un Uri de la lista. Si no existe, no hace nada.
     */
    fun removeUri(uri: Uri) {
        _urlList.update { current ->
            if (current.contains(uri)) current - uri else current
        }
    }

    /**
     * Vacía la lista.
     */
    fun clear() {
        _urlList.value = emptyList()
    }

    private val _notesTextFieldValue = MutableStateFlow(TextFieldValue(""))
    val notesTextFieldValue: StateFlow<TextFieldValue> = _notesTextFieldValue.asStateFlow()
    fun setNotesTextFieldValue(value: TextFieldValue) {
        _notesTextFieldValue.value = value
    }

    val isValidSeed: StateFlow<Boolean> = combine(
        nameTextFieldValue,
        urlList
    ) { name, urls ->
        name.text.isNotBlank() && name.text.length >= 3 && urls.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private fun clearFields() {
        _nameTextFieldValue.value = TextFieldValue("")
        _newUrlTextFieldValue.value = TextFieldValue("")
        _notesTextFieldValue.value = TextFieldValue("")
        clear()
        _collectionId.value = 0
        Timber.d("Fields cleared")
    }

    fun saveSeed( onSuccess: () -> Unit ) {
        viewModelScope.launch {
            try {
                val newSeed = LinkSeedEntity(
                    name = nameTextFieldValue.value.text,
                    links = urlList.value,
                    collection = _collectionId.value,
                    notes = notesTextFieldValue.value.text
                )
                val newSeedId = seedRepository.insert(newSeed)
                Timber.d("Seed saved with id: $newSeedId")
                onSuccess()
                clearFields()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}