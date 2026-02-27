package com.habitiora.linkarium.ui.screens.plantSeed

import com.habitiora.linkarium.core.SnackbarMessage
import com.habitiora.linkarium.ui.utils.pubsAndSubs.AddSeedEventBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.NavigationEventBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.SnackbarEventBus
import javax.inject.Inject

class PlantSeedEventCoordinator @Inject constructor(
    private val snackbarEventBus: SnackbarEventBus,
    private val addSeedEventBus: AddSeedEventBus,
    private val navigationEventBus: NavigationEventBus
) {
    val addSeedEvents = addSeedEventBus.events

    suspend fun updateEnable(isValid: Boolean) = addSeedEventBus.updateEnable(isValid)
    suspend fun showError(message: String) = snackbarEventBus.postMessage(SnackbarMessage.Error(message))
    suspend fun showInfo(message: String) = snackbarEventBus.postMessage(SnackbarMessage.Info(message))
    suspend fun navigateBack() = navigationEventBus.back()
}