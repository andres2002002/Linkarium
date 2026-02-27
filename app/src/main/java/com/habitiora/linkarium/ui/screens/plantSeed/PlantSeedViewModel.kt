package com.habitiora.linkarium.ui.screens.plantSeed

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.core.UriUtils.toUriSafe
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.usecase.LinkEntryImpl
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionInput
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PlantSeedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val useCases: PlantSeedUseCases,
    private val coordinator: PlantSeedEventCoordinator
) : ViewModel() {

    private val seedId: Long? = savedStateHandle["seedId"]

    // Estado interno mutable
    private val _state = MutableStateFlow(PlantSeedUiState())

    // Estado público inmutable combinado reactivamente con la DB
    val uiState: StateFlow<PlantSeedUiState> = combine(
        _state,
        useCases.getAllGardens(),
        useCases.getSelectedGardenIndex()
    ) { state, gardens, selectedIndex ->
        val selectedGarden = gardens.getOrNull(selectedIndex) ?: DatabaseContract.LinkGarden.Empty
        val isValid = validateSeed(state)

        state.copy(
            gardens = gardens,
            selectedGarden = selectedGarden,
            isValidSeed = isValid
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlantSeedUiState()
    )

    private var editingSeed: LinkSeed? = null
    private var cacheEntryId: Long = 0
    private var willUpdateOrder = false

    init {
        loadSeedIfExists()
        syncValidState()
        listenAddSeedEvent()
    }

    fun onEvent(event: PlantSeedEvent) {
        viewModelScope.launch {
            when (event) {
                is PlantSeedEvent.OnGardenChange -> useCases.selectGarden(event.index)
                is PlantSeedEvent.OnNameNotesTextFieldValueChange -> updateNameNotes(event.key, event.value)
                is PlantSeedEvent.OnNewEntryTextFieldValueChange -> updateNewEntry(event.key, event.value)
                is PlantSeedEvent.OnCoverTextFieldValueChange -> updateCover(event.value)
                PlantSeedEvent.OnAddLink -> addEntryOfCurrent()
                is PlantSeedEvent.OnEditLink -> editEntry(event.link)
                is PlantSeedEvent.OnRemoveLink -> removeEntry(event.link)
                is PlantSeedEvent.OnMoveLink -> moveEntry(event.from, event.to)
                PlantSeedEvent.ConsumeStatusAndBackStack -> consumeStatusAndBackStack()
            }
        }
    }
    private fun syncValidState() {
        viewModelScope.launch {
            uiState.collect { state ->
                coordinator.updateEnable(state.isValidSeed)
            }
        }
    }

    private fun listenAddSeedEvent() {
        viewModelScope.launch {
            coordinator.addSeedEvents.collect {
                saveSeed()
            }
        }
    }

    private fun loadSeedIfExists() {
        viewModelScope.launch {
            try {
                seedId?.let { id ->
                    val seed = useCases.getSeedById(id)?: return@let
                    setupEditMode(seed)
                } ?: run {
                    _state.update { it.copy(isEditMode = false) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading seed with id: $seedId")
                coordinator.showError("Error al cargar la semilla")
            }
        }
    }

    private fun setupEditMode(seed: LinkSeed) {
        editingSeed = seed

        _state.update { current ->
            current.copy(
                isEditMode = true,
                nameNotes = LabelDescriptionInput(
                    label = TextFieldValue(seed.name),
                    description = TextFieldValue(seed.notes.orEmpty())
                ),
                cover = TextFieldValue(seed.coverUri?.toString().orEmpty()),
            )
        }

        if (seed.links.size == 1) {
            val entry = seed.links.first()
            cacheEntryId = entry.id
            _state.update {
                it.copy(
                    newEntry = LinkEntryInput(
                        url = TextFieldValue(entry.uri.toString()),
                        label = TextFieldValue(entry.label.orEmpty()),
                        note = TextFieldValue(entry.note.orEmpty())
                    )
                )
            }
        } else if (seed.links.isNotEmpty()) {
            _state.update {
                it.copy(entries = seed.links)
            }
        }

        viewModelScope.launch { updateCoverImageUri() }
    }

    private fun updateNameNotes(key: LabelDescriptionInput.Key, value: TextFieldValue) {
        _state.update { current ->
            val updated = current.nameNotes.setValue(key, value)
            current.copy(nameNotes = updated)
        }
    }

    private fun updateNewEntry(key: LinkEntryInput.Key, value: TextFieldValue) {
        _state.update { current ->
            val updated = current.newEntry.setValue(key, value)
            current.copy(newEntry = updated)
        }
    }

    private suspend fun updateCover(value: TextFieldValue) {
        _state.update { it.copy(cover = value) }
        updateCoverImageUri()
    }

    private suspend fun addEntryOfCurrent(): Result<Boolean> = runCatching {
        val currentState = _state.value
        val urlString = currentState.newEntry.url.text
        val uri = urlString.trim().toUriSafe() ?: throw IllegalArgumentException("Invalid URL")

        if (!useCases.isValidResource(uri)) throw IllegalArgumentException("Invalid URI")


        if (currentState.cover.text.isBlank()) {
            extractThumbnailFromUriAsync(uri)
        }

        val entry = LinkEntryImpl(
            id = cacheEntryId,
            uri = uri,
            label = currentState.newEntry.label.text.ifBlank { null },
            note = currentState.newEntry.note.text.ifBlank { null }
        )
        val isDuplicate = currentState.entries.any { it.uri == entry.uri }

        if (isDuplicate) {
            viewModelScope.launch { coordinator.showInfo("URL ya agregada") }
            return@runCatching false
        }

        _state.update {
            it.copy(
                entries = it.entries + entry,
                newEntry = LinkEntryInput() // Limpiar fields
            )
        }

        cacheEntryId = 0

        updateCoverImageUri()
        Timber.i("Entry added successfully")
        true
    }

    private suspend fun editEntry(entry: LinkEntry) {
        val currentState = _state.value
        if (!currentState.entries.contains(entry)) return

        cacheEntryId = entry.id
        addEntryOfCurrent() // Intenta guardar lo que hay actualmente en el field

        _state.update {
            it.copy(
                entries = it.entries - entry,
                newEntry = LinkEntryInput(
                    url = TextFieldValue(entry.uri.toString()),
                    label = TextFieldValue(entry.label.orEmpty()),
                    note = TextFieldValue(entry.note.orEmpty())
                )
            )
        }
        updateCoverImageUri()
    }

    private suspend fun removeEntry(entry: LinkEntry) {
        _state.update { it.copy(entries = it.entries - entry) }
        updateCoverImageUri()
    }

    private fun moveEntry(from: Int, to: Int) {
        if (from == to) return
        willUpdateOrder = true
        _state.update { current ->
            val newList = current.entries.toMutableList().apply { add(to, removeAt(from)) }
            current.copy(entries = newList)
        }
    }

    private fun consumeStatusAndBackStack() {
        _state.update { it.copy(addSeedStatus = ProcessStatus.Empty) }
        viewModelScope.launch { coordinator.navigateBack() }
    }

    private fun saveSeed() {
        viewModelScope.launch {
            try {
                val resultAddEntry = addEntryOfCurrent()
                Timber.d("Entry added: $resultAddEntry")
                awaitFrame()
                val currentState = uiState.first() // Tomamos el estado sincronizado con la UI final
                val seed = createSeedFromState(currentState)

                val result = useCases.saveSeed(seed, currentState.isEditMode)

                result.fold(
                    onSuccess = {
                        coordinator.showInfo("Semilla guardada")
                        _state.value = PlantSeedUiState() // Reset total
                        willUpdateOrder = false
                        _state.update { it.copy(addSeedStatus = ProcessStatus.Success(true)) }
                    },
                    onFailure = {
                        coordinator.showError("No se pudo guardar la semilla")
                        _state.update { it.copy(addSeedStatus = ProcessStatus.Error("Error al guardar")) }
                    }
                )
            } catch (e: SQLiteException) {
                coordinator.showError("Error de base de datos al guardar")
            } catch (e: Exception) {
                coordinator.showError("Error inesperado al guardar")
            }
        }
    }

    private fun createSeedFromState(state: PlantSeedUiState): LinkSeedImpl {
        val finalEntries = if (willUpdateOrder) {
            state.entries.mapIndexed { index, linkEntry ->
                LinkEntryImpl(
                    id = linkEntry.id, uri = linkEntry.uri,
                    label = linkEntry.label, note = linkEntry.note, order = index
                )
            }
        } else state.entries
        return LinkSeedImpl(
            id = editingSeed?.id ?: 0,
            name = state.nameNotes.label.text,
            coverUri = state.coverImageUri,
            links = finalEntries,
            gardenId = state.selectedGarden.id,
            order = editingSeed?.order ?: 0,
            notes = state.nameNotes.description.text.ifBlank { null },
            tags = emptyList(),
            modifiedAt = LocalDateTime.now()
        )
    }

    private fun extractThumbnailFromUriAsync(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val extractedUri = useCases.extractThumbnail(uri)
            if (extractedUri != null) {
                _state.update { it.copy(cover = TextFieldValue(extractedUri.toString())) }
                updateCoverImageUri()
            }
        }
    }

    private suspend fun updateCoverImageUri() {
        val currentState = _state.value
        val manualUri = currentState.cover.text.takeIf { it.isNotBlank() }?.let { runCatching { it.toUri() }.getOrNull() }

        if (manualUri != null) {
            _state.update { it.copy(coverImageUri = manualUri) }
            return
        }

        // Fallback al primer entry
        val fallbackEntryUri = currentState.entries.firstOrNull()?.uri
        if (fallbackEntryUri != null) {
            Timber.d("Extracting thumbnail from fallback URI: $fallbackEntryUri")
            val extracted = useCases.extractThumbnail(fallbackEntryUri)
            Timber.d("Extracted thumbnail: $extracted")
            _state.update { it.copy(coverImageUri = extracted) }
            Timber.i("Cover image URI updated")
        } else {
            _state.update { it.copy(coverImageUri = null) }
        }
    }

    private fun validateSeed(state: PlantSeedUiState): Boolean {
        val name = state.nameNotes.label.text
        val hasValidName = name.isNotBlank() && name.length >= 3 // MIN_NAME_LENGTH
        val hasValidEntry = state.entries.isNotEmpty() || useCases.isValidResourceString(state.newEntry.url.text)
        return hasValidName && hasValidEntry
    }
}