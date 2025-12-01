package com.habitiora.linkarium.ui.screens.showGarden

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.scaffold.dialogs.DialogType
import com.habitiora.linkarium.ui.scaffold.dialogs.MessageValues
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.MessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShowGardenViewModel @Inject constructor(
    private val gardenRepository: LinkGardenRepository,
    private val seedRepository: LinkSeedRepository,
    private val gardenBus: GardenBus,
    private val messageBus: MessageBus
): ViewModel() {

    // region State Properties
    val gardens: StateFlow<List<LinkGarden>> = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedPageIndex = gardenBus.selectedGardenIndex
    val selectedPageIndex: StateFlow<Int> = combine(_selectedPageIndex, gardens) { index, gardens ->
        if (gardens.isEmpty()) return@combine 0
        index.coerceIn(gardens.indices)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val seeds: Flow<PagingData<LinkSeed>> =
        combine(_selectedPageIndex, gardens) { index, gardens ->
            gardens.getOrNull(index)
        }.distinctUntilChanged().flatMapLatest { garden ->
            seedRepository.getSeedsByGarden(garden?.id?:0)
        }.cachedIn(viewModelScope)

    private val _openGardenDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val openGardenDialog: StateFlow<Boolean> = _openGardenDialog.asStateFlow()

    // endregion

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

    /** Llamar cuando el pager settle cambie (desde UI) */
    fun onUserSwipedToPage(page: Int) {
        gardenBus.selectGarden(page)
    }
}