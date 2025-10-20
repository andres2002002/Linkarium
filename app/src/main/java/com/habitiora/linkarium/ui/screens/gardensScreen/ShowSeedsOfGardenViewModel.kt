package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.scaffold.dialogs.DialogType
import com.habitiora.linkarium.ui.scaffold.dialogs.MessageValues
import com.habitiora.linkarium.ui.utils.pubsAndSubs.MessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShowSeedsOfGardenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val seedRepository: LinkSeedRepository,
    private val messageBus: MessageBus
) : ViewModel() {
    val gardenId: Long = checkNotNull(savedStateHandle["gardenId"])

    val seeds: StateFlow<PagingData<LinkSeed>> = seedRepository.getSeedsByGarden(gardenId)
        .cachedIn(viewModelScope)
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PagingData.empty()
        )

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
}