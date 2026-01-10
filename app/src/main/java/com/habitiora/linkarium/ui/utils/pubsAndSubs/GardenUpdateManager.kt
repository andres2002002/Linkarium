package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.domain.model.LinkGarden
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton manager responsible for handling updates to [LinkGarden] objects across the application.
 *
 * This class acts as a central communication hub (Publisher/Subscriber pattern) where components can
 * listen for garden updates via [gardenUpdate] or trigger updates using [setGardenUpdate].
 * It effectively bridges parts of the UI that need to react to changes in a specific garden instance.
 *
 * @property gardenUpdate A [StateFlow][kotlinx.coroutines.flow.StateFlow] emitting the most recently updated [LinkGarden], or null if no update has occurred/been cleared.
 */
@Singleton
class GardenUpdateManager @Inject constructor(){
    private val _gardenUpdate = MutableStateFlow<LinkGarden?>(null)
    val gardenUpdate = _gardenUpdate.asStateFlow()
    fun setGardenUpdate(garden: LinkGarden?) {
        Timber.d("Garden update set to: ${garden?.id}")
        _gardenUpdate.value = garden
    }
}