package com.habitiora.linkarium.ui.scaffold

import androidx.lifecycle.ViewModel
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenUpdateManager
import com.habitiora.linkarium.ui.utils.pubsAndSubs.MessageBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.SnackbarEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScaffoldViewModel @Inject constructor(
    private val messageBus: MessageBus,
    private val snackbarEventBus: SnackbarEventBus,
    private val gardenUpdateManager: GardenUpdateManager
) : ViewModel() {
    val message = messageBus.message
    fun dismissDialog(){
        messageBus.pubMessage(null)
    }

    val snackbarEvents = snackbarEventBus.events

    val gardenUpdate = gardenUpdateManager.gardenUpdate
    fun consumeGarden(){
        gardenUpdateManager.setGardenUpdate(null)
    }
}