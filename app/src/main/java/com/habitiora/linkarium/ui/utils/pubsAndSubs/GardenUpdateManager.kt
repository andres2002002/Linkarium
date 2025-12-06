package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.domain.model.LinkGarden
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenUpdateManager @Inject constructor(){
    private val _gardenUpdate = MutableStateFlow<LinkGarden?>(null)
    val gardenUpdate = _gardenUpdate.asStateFlow()
    fun setGardenUpdate(garden: LinkGarden?) {
        Timber.d("Garden update set to: ${garden?.id}")
        _gardenUpdate.value = garden
    }
}