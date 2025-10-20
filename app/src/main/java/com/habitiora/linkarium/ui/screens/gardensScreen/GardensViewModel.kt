package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GardensViewModel  @Inject constructor(
    private val gardenRepository: LinkGardenRepository
) : ViewModel() {
    val gardens = gardenRepository.getAll()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}