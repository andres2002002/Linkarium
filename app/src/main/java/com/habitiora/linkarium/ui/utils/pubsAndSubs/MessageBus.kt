package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.ui.scaffold.dialogs.MessageValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A central communication bus implemented as a Singleton for publishing and subscribing to message events,
 * primarily used for triggering UI dialogs or global notifications.
 *
 * This class utilizes [MutableStateFlow] to hold the current message state. Observers can subscribe to
 * the [message] flow to receive updates whenever a new message is published via [pubMessage].
 *
 * Typical usage involves pushing a [MessageValues] object (containing dialog content like title, description,
 * and button actions) to trigger a global overlay or dialog within the UI scaffold.
 *
 * @property message A hot [StateFlow][kotlinx.coroutines.flow.StateFlow] that emits the current [MessageValues] or null.
 *                   UI components should collect this flow to react to new messages.
 */
@Singleton
class MessageBus @Inject constructor() {
    private val _message: MutableStateFlow<MessageValues?> = MutableStateFlow(null)
    val message = _message.asStateFlow()
    fun pubMessage(message: MessageValues?) {
        _message.value = message
    }
}