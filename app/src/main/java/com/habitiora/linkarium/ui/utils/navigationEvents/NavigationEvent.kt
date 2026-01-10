package com.habitiora.linkarium.ui.utils.navigationEvents

import com.habitiora.linkarium.ui.navigation.Screens

sealed interface NavigationEvent {

    data class To(
        val screen: Screens,
        val id: Long = -1,
        val inclusive: Boolean = false
    ) : NavigationEvent

    data object Back : NavigationEvent
}
