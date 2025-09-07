package com.habitiora.linkarium.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.habitiora.linkarium.R

sealed class Screens(
    val route: String,
    @StringRes val normalTitle: Int,
    @StringRes val creativeTitle: Int = normalTitle,
    @DrawableRes val iconSelect: Int,
    @DrawableRes val iconUnselect: Int = iconSelect
) {
    data object ShowGarden: Screens(
        route = "show_garden",
        normalTitle = R.string.link_garden,
        creativeTitle = R.string.link_garden,
        iconSelect = R.drawable.round_home_24,
        iconUnselect = R.drawable.outline_home_24
    )
    data object Gardens: Screens(
        route = "gardens",
        normalTitle = R.string.gardens,
        creativeTitle = R.string.gardens,
        iconSelect = R.drawable.round_collections_bookmark_24,
        iconUnselect = R.drawable.outline_collections_bookmark_24
    )
    data object Settings: Screens(
        route = "settings",
        normalTitle = R.string.settings,
        creativeTitle = R.string.settings,
        iconSelect = R.drawable.ic_launcher_foreground
    )
    data object PlantNew: Screens(
        route = "plant_new",
        normalTitle = R.string.plant,
        creativeTitle = R.string.plant,
        iconSelect = R.drawable.ic_launcher_foreground
    )
}