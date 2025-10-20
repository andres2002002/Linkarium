package com.habitiora.linkarium.ui.screens.showGarden

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.data.repository.ExportRepository
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.scaffold.dialogs.DialogType
import com.habitiora.linkarium.ui.scaffold.dialogs.MessageValues
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.MessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShowGardenViewModel @Inject constructor(
    private val gardenRepository: LinkGardenRepository,
    private val seedRepository: LinkSeedRepository,
    private val gardenBus: GardenBus,
    private val messageBus: MessageBus,
    private val exportRepository: ExportRepository
): ViewModel() {
    val gardens: StateFlow<List<LinkGarden>> = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedGardenId = gardenBus.selectedGardenId
    fun setSelectedGardenId(id: Long) = gardenBus.selectGarden(id)

    @OptIn(ExperimentalCoroutinesApi::class)
    val garden: StateFlow<LinkGarden?> = _selectedGardenId
        .flatMapLatest { id ->
            gardenRepository.getById(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val seeds: StateFlow<PagingData<LinkSeed>> =
        _selectedGardenId
            .flatMapLatest { id ->
                seedRepository.getSeedsByGarden(id).cachedIn(viewModelScope)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = PagingData.empty()
            )

    private val _openGardenDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val openGardenDialog: StateFlow<Boolean> = _openGardenDialog.asStateFlow()
    fun setOpenGardenDialog(value: Boolean) {
        _openGardenDialog.value = value
    }
    fun onDeleteLinkSeed(linkSeed: LinkSeed) {
        val message = MessageValues(
            type = DialogType.Warning,
            title = "Delete LinkSeed",
            message = "Are you sure you want to delete ${linkSeed.name}?",
            buttons = mapOf(
                "delete" to {
                    viewModelScope.launch {
                        seedRepository.delete(linkSeed)
                        messageBus.pubMessage(null)
                        Timber.i("LinkSeed deleted: ${linkSeed.name}, with id: ${linkSeed.id}")
                    }
                },
                "cancel" to {
                    messageBus.pubMessage(null)
                    Timber.d("Delete LinkSeed cancelled: ${linkSeed.name}, with id: ${linkSeed.id}")
                }
            )
        )
        messageBus.pubMessage(message)
        Timber.d("Message published for LinkSeed: ${linkSeed.name}, with id: ${linkSeed.id}")
    }

    fun exportGardens(uri: Uri, context: Context) {
        viewModelScope.launch {
            context.contentResolver.openOutputStream(uri)?.use {
                val result = when (uri.toString().substringAfterLast('.')) {
                    "txt" -> exportRepository.exportGardensTxt(it)
                    "json" -> exportRepository.exportGardensJson(it)
                    "pdf" -> exportRepository.exportGardensPdf(it)
                    else -> Result.failure(IllegalArgumentException("Invalid file extension"))
                }
                if (result.isSuccess) {
                    Timber.d("Export successful")
                } else {
                    Timber.d("Export failed")
                }
            }
        }
    }
}