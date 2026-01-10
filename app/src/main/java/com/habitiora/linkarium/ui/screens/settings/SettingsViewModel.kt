package com.habitiora.linkarium.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.core.exporters.ExportRequest
import com.habitiora.linkarium.core.exporters.ExportState
import com.habitiora.linkarium.data.local.usecase.ExportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    //private val settingsRepository: SettingsRepository
    private val exportUseCase: ExportUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ExportState>(ExportState.Idle)
    val state: StateFlow<ExportState> = _state

    fun export(request: ExportRequest) {
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
}