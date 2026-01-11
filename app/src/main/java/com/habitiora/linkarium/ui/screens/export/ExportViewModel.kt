package com.habitiora.linkarium.ui.screens.export

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.core.exporters.ExportContent
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportState
import com.habitiora.linkarium.data.local.usecase.ExportUseCase
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.ui.utils.ExportSelectionMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportUseCase: ExportUseCase,
    private val gardenRepository: LinkGardenRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ExportState>(ExportState.Idle)
    val state: StateFlow<ExportState> = _state
    private val _exportFormat = MutableStateFlow<ExportFormat?>(null)
    val exportFormat: StateFlow<ExportFormat?> = _exportFormat

    private val _exportSelectionMode = MutableStateFlow(ExportSelectionMode.AllGardens)
    val exportSelectionMode: StateFlow<ExportSelectionMode> = _exportSelectionMode

    val gardens = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _gardensSelected = MutableStateFlow<List<Long>>(emptyList())
    val gardensSelected: StateFlow<List<Long>> = _gardensSelected

    fun setExportFormat(format: ExportFormat) {
        _exportFormat.value = format
    }
    fun selectionChange(mode: ExportSelectionMode){
        _exportSelectionMode.value = mode
    }

    fun export(uri: Uri){
        exportInternal(prepareRequest(uri))
    }

    private fun prepareRequest(uri: Uri): ExportRequest {
        return ExportRequest(
            _exportSelectionMode.value.toExportContent(_gardensSelected.value),
            _exportFormat.value ?: ExportFormat.Json,
            uri
        )
    }

    private fun exportInternal(request: ExportRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ExportState.Loading
            runCatching {
                exportUseCase(request)
            }.onSuccess {
                Timber.d("Export success")
                _state.value = ExportState.Success(request.uri)
            }.onFailure {
                Timber.e(it, "Export failed")
                _state.value = ExportState.Error(it)
            }
        }
    }

    fun selectGarden(id: Long){
        _gardensSelected.update {
            if (it.contains(id)) it - id
            else it + id
        }
    }
}