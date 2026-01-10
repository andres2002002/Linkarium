package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.ui.scaffold.dialogs.MessageValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A central communication bus implemented as a Singleton for publishing and subscribing to message events,
 * primarily used for triggering UI dialogs.
 */
@Singleton
class MessageBus @Inject constructor() {
    private val _message: MutableStateFlow<MessageValues?> = MutableStateFlow(null)
    val message = _message.asStateFlow()
    fun pubMessage(message: MessageValues?) {
        _message.value = message
    }
}