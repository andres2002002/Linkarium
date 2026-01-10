package com.habitiora.linkarium.ui.utils.pubsAndSubs

import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.utils.navigationEvents.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationEventBus @Inject constructor() {

    private val _events = MutableSharedFlow<NavigationEvent>(
        extraBufferCapacity = 1
    )

    val events = _events.asSharedFlow()

    fun emitEvent(event: NavigationEvent){
        _events.tryEmit(event)
    }

    fun navigate(
        screen: Screens,
        id: Long = -1,
        inclusive: Boolean = false
    ) {
        _events.tryEmit(
            NavigationEvent.To(
                screen = screen,
                id = id,
                inclusive = inclusive
            )
        )
    }

    fun back() {
        _events.tryEmit(NavigationEvent.Back)
    }
}
