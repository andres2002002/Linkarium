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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.habitiora.linkarium.core.SnackbarMessage
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.usecase.LinkEntryImpl
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionTextFieldValues
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryTextFieldValues
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenSelectionManager
import com.habitiora.linkarium.ui.utils.pubsAndSubs.SnackbarEventBus
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
    private val savedStateHandle: SavedStateHandle,
    private val seedRepository: LinkSeedRepository,
    private val gardenRepository: LinkGardenRepository,
    private val gardenSelectionManager: GardenSelectionManager,
    private val snackbarEventBus: SnackbarEventBus
) : ViewModel() {

    // region State Properties

    private val seedId: Long? = savedStateHandle["seedId"]

    val gardens: StateFlow<List<LinkGarden>> = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private val _selectedGardenIndex = gardenSelectionManager.selectedGardenIndex

    val garden: StateFlow<LinkGarden> = combine(_selectedGardenIndex, gardens) { index, gardens ->
        gardens.getOrNull(index) ?: DatabaseContract.LinkGarden.Empty
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DatabaseContract.LinkGarden.Empty
    )

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _nameNotesTextFieldValue = MutableStateFlow(LabelDescriptionTextFieldValues())
    val nameNotesTextFieldValue: StateFlow<LabelDescriptionTextFieldValues> =
        _nameNotesTextFieldValue.asStateFlow()

    private val _newEntryTextFieldValues = MutableStateFlow(LinkEntryTextFieldValues())
    val newEntryTextFieldValues: StateFlow<LinkEntryTextFieldValues> =
        _newEntryTextFieldValues.asStateFlow()

    private val _entriesList = MutableStateFlow<List<LinkEntry>>(emptyList())
    val entries: StateFlow<List<LinkEntry>> = _entriesList.asStateFlow()

    val isValidSeed: StateFlow<Boolean> = combine(
        _nameNotesTextFieldValue,
        entries,
        _newEntryTextFieldValues
    ) { nameNotes, entries, newEntry ->
        val name = nameNotes.label.text
        val hasValidName = name.isNotBlank() && name.length >= MIN_NAME_LENGTH
        val hasValidEntry = entries.isNotEmpty() || isLikelyValidWebUri(newEntry.url.text)
        hasValidName && hasValidEntry
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // endregion

    // region Private Properties
    private var editingSeed: LinkSeed? = null
    private var cacheEntryId: Long = 0
    private var willUpdateOrder = false

    // endregion

    // region Initialization

    init {
        loadSeedIfExists()
    }

    private fun loadSeedIfExists() {
        viewModelScope.launch {
            try {
                val seed = seedId?.let { id ->
                    seedRepository.getById(id).first()
                }

                if (seed != null) {
                    setupEditMode(seed)
                } else {
                    Timber.d("No seed found with id: $seedId")
                    _isEditMode.value = false
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading seed with id: $seedId")
                showErrorMessage("Error al cargar la semilla")
            }
        }
    }

    private fun setupEditMode(seed: LinkSeed) {
        Timber.d("Loading seed for editing: id=${seed.id}, name=${seed.name}")

        editingSeed = seed
        _isEditMode.value = true

        // Cargar nombre y notas
        _nameNotesTextFieldValue.value = LabelDescriptionTextFieldValues(
            label = TextFieldValue(seed.name),
            description = TextFieldValue(seed.notes.orEmpty())
        )

        // Cargar enlaces según la cantidad
        when {
            seed.links.isEmpty() -> {
                Timber.d("No entries to load")
            }

            seed.links.size == 1 -> {
                loadSingleEntry(seed.links.first())
            }

            else -> {
                loadMultipleEntries(seed.links)
            }
        }
    }

    private fun loadSingleEntry(entry: LinkEntry) {
        Timber.d("Loading single entry: ${entry.uri}")
        cacheEntryId = entry.id
        _newEntryTextFieldValues.value = LinkEntryTextFieldValues(
            url = TextFieldValue(entry.uri.toString()),
            label = TextFieldValue(entry.label.orEmpty()),
            note = TextFieldValue(entry.note.orEmpty())
        )
    }

    private fun loadMultipleEntries(links: List<LinkEntry>) {
        Timber.d("Loading ${links.size} entries")
        _entriesList.value = links
    }

    // endregion

    // region Public Actions

    fun setGardenIndex(index: Int) = gardenSelectionManager.selectGarden(index)

    fun updateNameNotesTextFieldValue(key: String, value: TextFieldValue) {
        _nameNotesTextFieldValue.update {
            when (key) {
                LabelDescriptionTextFieldValues.LABEL_KEY -> it.copy(label = value)
                LabelDescriptionTextFieldValues.DESCRIPTION_KEY -> it.copy(description = value)
                else -> it
            }
        }
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

    fun addEntryOfCurrent(): Boolean {
        val urlString = _newEntryTextFieldValues.value.url.text
        val uri = urlString.trim().toUriOrNull() ?: run {
            Timber.d("Invalid URL format: $urlString")
            return false
        }

        if (!isLikelyValidWebUri(uri)) {
            Timber.d("URL validation failed: $uri")
            return false
        }

        val label = _newEntryTextFieldValues.value.label.text.ifBlank { null }
        val note = _newEntryTextFieldValues.value.note.text.ifBlank { null }

        val entry = LinkEntryImpl(
            id = cacheEntryId,
            uri = uri,
            label = label,
            note = note
        )

        val added = addEntry(entry)
        if (added) {
            clearEntryTextFields()
            cacheEntryId = 0
        }

        return added
    }

    fun editEntry(entry: LinkEntry) {
        val index = _entriesList.value.indexOf(entry)

        if (index == -1) {
            Timber.d("Entry not found in list")
            cacheEntryId = 0
            return
        }

        cacheEntryId = entry.id
        addEntryOfCurrent()
        removeEntry(entry)

        _newEntryTextFieldValues.value = LinkEntryTextFieldValues(
            url = TextFieldValue(entry.uri.toString()),
            label = TextFieldValue(entry.label.orEmpty()),
            note = TextFieldValue(entry.note.orEmpty())
        )
    }

    fun removeEntry(entry: LinkEntry) {
        _entriesList.update { current ->
            current - entry
        }
    }

    fun moveEntry(from: Int, to: Int) {
        if (from == to) return

        willUpdateOrder = true
        _entriesList.update { current ->
            current.toMutableList().apply {
                add(to, removeAt(from))
            }
        }
    }

    fun saveSeed(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Intentar agregar entrada actual si existe
            addEntryOfCurrent()

            try {
                saveSeedInternal(onSuccess)
            } catch (e: SQLiteException) {
                Timber.e(e, "SQLite error saving seed")
                showErrorMessage("Error de base de datos al guardar")
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error saving seed")
                showErrorMessage("Error inesperado al guardar")
            }
        }
    }

    fun clear() {
        _entriesList.value = emptyList()
        willUpdateOrder = false
    }

    // endregion

    // region Private Helper Methods

    private fun addEntry(entry: LinkEntry): Boolean {
        val isDuplicate = _entriesList.value.any { it.uri == entry.uri }

        if (isDuplicate) {
            showInfoMessage("URL ya agregada")
            return false
        }

        _entriesList.update { it + entry }
        return true
    }

    private fun updateOrders() {
        _entriesList.update { current ->
            current.mapIndexed { index, linkEntry ->
                LinkEntryImpl(
                    id = linkEntry.id,
                    uri = linkEntry.uri,
                    label = linkEntry.label,
                    note = linkEntry.note,
                    order = index
                )
            }
        }
    }

    private suspend fun saveSeedInternal(onSuccess: () -> Unit) {
        if (willUpdateOrder) {
            updateOrders()
        }

        val seed = createSeedFromCurrentState()
        Timber.d("Saving seed: id=${seed.id}, name=${seed.name}, entries=${seed.links.size}")

        val result = if (_isEditMode.value) {
            seedRepository.update(seed)
        } else {
            seedRepository.insert(seed)
        }

        result.fold(
            onSuccess = { savedId ->
                Timber.d("Seed saved successfully with id: $savedId")
                clearAllFields()
                onSuccess()
            },
            onFailure = { error ->
                Timber.e(error, "Failed to save seed")
                showErrorMessage("No se pudo guardar la semilla")
            }
        )
    }

    private fun createSeedFromCurrentState(): LinkSeedImpl {
        return LinkSeedImpl(
            id = editingSeed?.id ?: 0,
            name = _nameNotesTextFieldValue.value.label.text,
            links = entries.value,
            gardenId = garden.value.id,
            order = editingSeed?.order?: 0,
            notes = _nameNotesTextFieldValue.value.description.text.ifBlank { null },
            tags = emptyList(),
            modifiedAt = LocalDateTime.now()
        )
    }

    private fun clearAllFields() {
        _nameNotesTextFieldValue.value = LabelDescriptionTextFieldValues()
        clearEntryTextFields()
        clear()
        Timber.d("All fields cleared")
    }

    private fun clearEntryTextFields() {
        _newEntryTextFieldValues.value = LinkEntryTextFieldValues()
    }

    private fun isLikelyValidWebUri(uri: Uri): Boolean {
        val uriString = uri.toString()
        return Patterns.WEB_URL.matcher(uriString).matches() ||
                uri.scheme in VALID_SCHEMES
    }

    private fun isLikelyValidWebUri(url: String): Boolean {
        val uri = url.trim().toUriOrNull() ?: return false
        return isLikelyValidWebUri(uri)
    }

    private fun String.toUriOrNull(): Uri? {
        return runCatching { toUri() }.getOrNull()
    }

    private fun showErrorMessage(message: String) {
        sendSnackbarMessage(SnackbarMessage.Error(message))
    }

    private fun showInfoMessage(message: String) {
        sendSnackbarMessage(SnackbarMessage.Info(message))
    }

    private fun sendSnackbarMessage(message: SnackbarMessage) {
        viewModelScope.launch {
            snackbarEventBus.postMessage(message)
        }
    }

    // endregion

    companion object {
        private const val MIN_NAME_LENGTH = 3
        private val VALID_SCHEMES = setOf("content", "file", "http", "https")
    }
}