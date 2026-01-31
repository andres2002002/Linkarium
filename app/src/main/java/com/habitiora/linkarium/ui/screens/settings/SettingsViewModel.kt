package com.habitiora.linkarium.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.data.repository.PreferencesRepository
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.utils.pubsAndSubs.NavigationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigationEventBus: NavigationEventBus,
    private val preferences: PreferencesRepository
) : ViewModel() {

    val isBiometricLockEnabled: StateFlow<Boolean> = preferences.userPreferences.map { it.useBiometricLock }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun updateBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            preferences.updateBiometricLock(enabled)
        }
    }
    fun navigateTo(screen: Screens) = navigationEventBus.navigate(screen)
}