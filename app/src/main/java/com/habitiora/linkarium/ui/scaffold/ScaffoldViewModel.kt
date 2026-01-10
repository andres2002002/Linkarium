package com.habitiora.linkarium.ui.scaffold

import androidx.lifecycle.ViewModel
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.utils.pubsAndSubs.AddSeedEventBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenUpdateManager
import com.habitiora.linkarium.ui.utils.pubsAndSubs.MessageBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.NavigationEventBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.SnackbarEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScaffoldViewModel @Inject constructor(
    private val messageBus: MessageBus,
    private val snackbarEventBus: SnackbarEventBus,
    private val addSeedEventBus: AddSeedEventBus,
    private val gardenUpdateManager: GardenUpdateManager,
    private val navigationBus: NavigationEventBus
) : ViewModel() {
    val message = messageBus.message
    val enableAddSeed = addSeedEventBus.enable

    fun emitEventAddSeed() = addSeedEventBus::emitEvent

    fun dismissDialog(){
        messageBus.pubMessage(null)
    }

    val snackbarEvents = snackbarEventBus.events

    val gardenUpdate = gardenUpdateManager.gardenUpdate
    fun consumeGarden(){
        gardenUpdateManager.setGardenUpdate(null)
    }

    val navigationEvents = navigationBus.events

    // Helpers opcionales (ergonomía)
    fun navigateTo(
        screen: Screens,
        id: Long = -1,
        inclusive: Boolean = false
    ) {
        navigationBus.navigate(screen, id, inclusive)
    }

    fun back() {
        navigationBus.back()
    }
}