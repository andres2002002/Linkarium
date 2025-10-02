package com.habitiora.linkarium.ui.screens.plantSeed

import android.database.sqlite.SQLiteConstraintException
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
import androidx.sqlite.SQLiteException
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.usecase.LinkEntryImpl
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionTextFieldValues
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryTextFieldValues
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.SeedManagerBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class PlantSeedViewModel @Inject constructor(
    private val seedRepository: LinkSeedRepository,
    private val gardenRepository: LinkGardenRepository,
    private val gardenBus: GardenBus,
    private val seedManagerBus: SeedManagerBus
) : ViewModel() {

    val gardens: StateFlow<List<LinkGarden>> = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val gardenId: StateFlow<Long> = gardenBus.selectedGardenId
    fun setGardenId(id: Long) = gardenBus.selectGarden(id)

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private var editingSeedId: Long? = null

    private val _nameNotesTextFieldValue = MutableStateFlow(LabelDescriptionTextFieldValues())
    val nameNotesTextFieldValue: StateFlow<LabelDescriptionTextFieldValues> = _nameNotesTextFieldValue.asStateFlow()
    fun updateNameNotesTextFieldValue(key: String, value: TextFieldValue) {
        _nameNotesTextFieldValue.update {
            when (key) {
                LabelDescriptionTextFieldValues.LABEL_KEY -> it.copy(label = value)
                LabelDescriptionTextFieldValues.DESCRIPTION_KEY -> it.copy(description = value)
                else -> it
            }
        }
    }

    fun clearNameNotesTextFieldValue() {
        _nameNotesTextFieldValue.value = LabelDescriptionTextFieldValues()
    }

    private val _newEntryTextFieldValues = MutableStateFlow(LinkEntryTextFieldValues())
    val newEntryTextFieldValues: StateFlow<LinkEntryTextFieldValues> = _newEntryTextFieldValues.asStateFlow()
    fun setNewEntryTextFieldValues(values: LinkEntryTextFieldValues) {
        _newEntryTextFieldValues.value = values
    }
    fun updateNewEntryTextFieldValues(key: String, value: TextFieldValue) {
        _newEntryTextFieldValues.update {
            when (key) {
                LinkEntryTextFieldValues.NOTE_KEY -> it.copy(note = value)
                LinkEntryTextFieldValues.URL_KEY -> it.copy(url = value)
                LinkEntryTextFieldValues.LABEL_KEY -> it.copy(label = value)
                else -> it
            }
        }
    }
    private fun clearEntryTextFields() {
        _newEntryTextFieldValues.value = LinkEntryTextFieldValues()
    }

    private fun isLikelyValidWebUri(uri: Uri): Boolean {
        val s = uri.toString()
        // chequeo simple con Patterns para evitar insertar basura
        return Patterns.WEB_URL.matcher(s).matches() || uri.scheme in listOf("content", "file")
    }

    private fun isLikelyValidWebUri(url: String): Boolean {
        val uri = runCatching { url.trim().toUri() }.getOrNull() ?: return false
        return isLikelyValidWebUri(uri)
    }

    private val _entriesList = MutableStateFlow<List<LinkEntry>>(emptyList())
    val entries: StateFlow<List<LinkEntry>> = _entriesList.asStateFlow()

    init {
        val seedToEdit = seedManagerBus.getAndConsumeSeedToEdit()
        if (seedToEdit != null) {
            editingSeedId = seedToEdit.id
            _isEditMode.value = true
            updateNameNotesTextFieldValue(
                LabelDescriptionTextFieldValues.LABEL_KEY,
                TextFieldValue(seedToEdit.name)
            )
            seedToEdit.notes?.let { notes ->
                updateNameNotesTextFieldValue(
                    LabelDescriptionTextFieldValues.DESCRIPTION_KEY,
                    TextFieldValue(notes)
                )
            }
            if (seedToEdit.links.size == 1) {
                setNewEntryTextFieldValues(
                    LinkEntryTextFieldValues(
                        url = TextFieldValue(seedToEdit.links[0].uri.toString()),
                        label = TextFieldValue(seedToEdit.links[0].label ?: ""),
                        note = TextFieldValue(seedToEdit.links[0].note ?: "")
                    )
                )
            }
            else _entriesList.value = seedToEdit.links
        }
        else {
            _isEditMode.value = false
        }
    }

    var cacheEntryId: Long = 0

    fun editEntry(entry: LinkEntry) {
        val index = _entriesList.value.indexOf(entry)
        // validar que el índice sea válido
        if (index != -1) {
            cacheEntryId = entry.id
            addEntryOfCurrent()
            removeEntry(entry)
            setNewEntryTextFieldValues(
                LinkEntryTextFieldValues(
                    url = TextFieldValue(entry.uri.toString()),
                    label = TextFieldValue(entry.label ?: ""),
                    note = TextFieldValue(entry.note ?: "")
                )
            )
        }
        else {
            cacheEntryId = 0
            Timber.d("Entry not found in list")
        }
    }

    fun addEntryOfCurrent(): Boolean {
        return if (addEntryOfCurrentInternal()) {
            clearEntryTextFields()
            true
        } else {
            val entry = _newEntryTextFieldValues.value
            Timber.d("Url not added: $entry")
            false
        }
    }

    private fun addEntryOfCurrentInternal(): Boolean {
        val urlString = _newEntryTextFieldValues.value.url.text
        val uri = runCatching { urlString.trim().toUri() }.getOrNull() ?: return false
        if (!isLikelyValidWebUri(uri)) return false
        val label = _newEntryTextFieldValues.value.label.text.ifBlank { null }
        val note = _newEntryTextFieldValues.value.note.text.ifBlank { null }
        addEntry(
            LinkEntryImpl(
                id = cacheEntryId,
                uri = uri,
                label = label,
                note = note
            )
        )
        cacheEntryId = 0
        return true
    }

    private fun addEntry(entry: LinkEntry) {
        _entriesList.update { current ->
            // LinkedHashSet preserva orden de inserción y evita duplicados
            LinkedHashSet(current).apply { add(entry) }.toList()
        }
    }

    fun removeEntry(entry: LinkEntry) {
        _entriesList.update { current ->
            if (current.contains(entry)) current - entry else current
        }
    }

    /**
     * Vacía la lista.
     */
    fun clear() {
        _entriesList.value = emptyList()
    }

    val isValidSeed: StateFlow<Boolean> = combine(
        _nameNotesTextFieldValue,
        entries,
        _newEntryTextFieldValues
    ) { nameNotes, entries, newEntry ->
        val name = nameNotes.label
        name.text.isNotBlank() && name.text.length >= 3 &&
                (entries.isNotEmpty() || isLikelyValidWebUri(newEntry.url.text))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private fun clearFields() {
        clearNameNotesTextFieldValue()
        clearEntryTextFields()
        clear()
        Timber.d("Fields cleared")
    }

    fun saveSeed(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (addEntryOfCurrentInternal()) saveSeedInternal(onSuccess)
            else saveSeedInternal(onSuccess)
        }
    }

    private suspend fun saveSeedInternal(onSuccess: () -> Unit) {
        try {
            val seed = LinkSeedImpl(
                id = editingSeedId ?: 0, // Usa el ID existente o 0 para una nueva entidad
                name = _nameNotesTextFieldValue.value.label.text,
                links = entries.value,
                gardenId = gardenId.value,
                notes = _nameNotesTextFieldValue.value.description.text,
                tags = emptyList(),
                modifiedAt = LocalDateTime.now()
            )
            val result = if (_isEditMode.value) seedRepository.update(seed)
            else seedRepository.insert(seed)

            if (result.isSuccess) {
                Timber.d("Seed saved/updated with id: ${result.getOrNull()?: seed.id}")
                clearFields()
                gardenRepository.clearCache(gardenId.value)
                onSuccess()
            } else {
                Timber.d("Seed not saved")
            }
        } catch (e: SQLiteException){
            Timber.e(e, "Error with SQLite")
        } catch (e: Exception) {
            Timber.e(e, "Error saving seed")
        }
    }
}