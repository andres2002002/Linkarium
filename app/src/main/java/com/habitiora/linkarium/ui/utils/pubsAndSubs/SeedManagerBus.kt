package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedManagerBus @Inject constructor() {
    private val _seedToEdit = MutableStateFlow<LinkSeed?>(null)
    fun pubSeedToEdit(seed: LinkSeed) {
        _seedToEdit.value = seed
    }
    fun getAndConsumeSeedToEdit(): LinkSeed? {
        val seed = _seedToEdit.value
        _seedToEdit.value = null
        return seed
    }
}