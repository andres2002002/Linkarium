package com.habitiora.linkarium.ui.screens.showGarden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
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
import javax.inject.Inject

@HiltViewModel
class ShowGardenViewModel @Inject constructor(
    private val gardenRepository: LinkGardenRepository
): ViewModel() {
    val gardens: StateFlow<List<LinkGarden>> = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val _selectedGardenId = MutableStateFlow<Long>(0)
    fun setSelectedGardenId(id: Long) {
        _selectedGardenId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedGarden: StateFlow<LinkGardenWithSeeds> = _selectedGardenId
        .flatMapLatest { id ->
            gardenRepository.getGardenWithSeeds(id).map {
                it ?: DatabaseContract.LinkGardenWithSeeds.Empty
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = DatabaseContract.LinkGardenWithSeeds.Empty
        )
}