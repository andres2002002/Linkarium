package com.habitiora.linkarium.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.utils.pubsAndSubs.NavigationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigationEventBus: NavigationEventBus
) : ViewModel() {
    fun navigateTo(screen: Screens) = navigationEventBus.navigate(screen)
}