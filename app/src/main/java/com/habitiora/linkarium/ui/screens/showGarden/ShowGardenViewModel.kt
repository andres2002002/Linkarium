package com.habitiora.linkarium.ui.screens.showGarden

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.repository.ExportRepository
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.SeedManagerBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShowGardenViewModel @Inject constructor(
    private val gardenRepository: LinkGardenRepository,
    private val gardenBus: GardenBus,
    private val seedManagerBus: SeedManagerBus,
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
    val selectedGarden: StateFlow<LinkGardenWithSeeds> = _selectedGardenId
        .map { id ->
            gardenRepository.getGardenWithSeeds(id) ?: DatabaseContract.LinkGardenWithSeeds.Empty
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DatabaseContract.LinkGardenWithSeeds.Empty
        )

    private val _openGardenDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val openGardenDialog: StateFlow<Boolean> = _openGardenDialog.asStateFlow()
    fun setOpenGardenDialog(value: Boolean) {
        _openGardenDialog.value = value
    }

    fun onEditLinkSeed(linkSeed: LinkSeed) {
        seedManagerBus.pubSeedToEdit(linkSeed)
        Timber.d("Edit LinkSeed: ${linkSeed.name}, with id: ${linkSeed.id}")
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