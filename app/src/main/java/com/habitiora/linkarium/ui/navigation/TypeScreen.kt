package com.habitiora.linkarium.ui.navigation

/**
 * Type of screen
 * @property showTopBar Boolean show top bar
 * @property showBottomBar Boolean show bottom bar
 * @property showFloatingActionButton Boolean show floating action button
 */
sealed class TypeScreen(
    open val showTopBar: Boolean,
    open val showBottomBar: Boolean,
    open val showFloatingActionButton: Boolean
) {
    /** Primary screen type with top bar, bottom bar and floating action button */
    data object Primary: TypeScreen(true, true, true)
    /** Secondary screen type with top bar and bottom bar */
    data object Secondary: TypeScreen(true, true, false)
    /** Tertiary screen type with top bar and floating action button */
    data object Tertiary: TypeScreen(true, false, false)
    /** Modal screen type with bottom bar and floating action button */
    data object Modal: TypeScreen(false, false, false)
    /** Custom screen type, can be used to create a custom screen type */
    data class Custom(
        override val showTopBar: Boolean,
        override val showBottomBar: Boolean,
        override val showFloatingActionButton: Boolean
    ): TypeScreen(showTopBar, showBottomBar, showFloatingActionButton)
}