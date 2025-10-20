package com.habitiora.linkarium.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.habitiora.linkarium.R

sealed class Screens(
    val baseRoute: String,
    @StringRes val normalTitle: Int,
    @StringRes val creativeTitle: Int = normalTitle,
    @DrawableRes val iconSelect: Int? = null,
    @DrawableRes val iconUnselect: Int? = iconSelect,
    val typeScreen: TypeScreen = TypeScreen.Primary
) {
    /** Ruta completa (con placeholders si aplica) */
    open val route: String get() = baseRoute

    /** Permite detectar rutas dinámicas (por ejemplo "show_seeds/3") */
    open fun matches(route: String?): Boolean =
        route?.substringBefore("?")?.startsWith(baseRoute) == true
    /**
     * Permite crear rutas dinámicas (por ejemplo "show_seeds/3").
     * Tiene valor por defecto -1 que se considera null
     * */
    open fun createRoute(id: Long = -1): String = baseRoute

    companion object{
        /** Registro automático de pantallas */
        val allScreens: List<Screens> = listOf(
            ShowGarden,
            Gardens,
            Settings,
            PlantNew,
            ShowSeeds
        )

        /** Búsqueda genérica de pantalla por route */
        fun fromRoute(route: String?): Screens? {
            return allScreens.firstOrNull { it.matches(route) }
        }
    }
    data object ShowGarden: Screens(
        baseRoute = "show_garden",
        normalTitle = R.string.link_garden,
        creativeTitle = R.string.link_garden,
        iconSelect = R.drawable.round_home_24,
        iconUnselect = R.drawable.outline_home_24
    )
    data object Gardens: Screens(
        baseRoute = "gardens",
        normalTitle = R.string.gardens,
        creativeTitle = R.string.gardens,
        iconSelect = R.drawable.round_collections_bookmark_24,
        iconUnselect = R.drawable.outline_collections_bookmark_24,
        typeScreen = TypeScreen.Primary
    )
    data object Settings: Screens(
        baseRoute = "settings",
        normalTitle = R.string.settings,
        creativeTitle = R.string.settings,
        typeScreen = TypeScreen.Secondary
    )
    data object PlantNew: Screens(
        baseRoute = "plant_new",
        normalTitle = R.string.plant,
        creativeTitle = R.string.plant,
        typeScreen = TypeScreen.Tertiary
    ){
        override val route = "$baseRoute?seedId={seedId}"

        override fun createRoute(id: Long): String {
            return if (id > 0) "$baseRoute?seedId=$id"
            else baseRoute
        }
    }

    data object ShowSeeds: Screens(
        baseRoute = "show_seeds",
        normalTitle = R.string.plant,
        creativeTitle = R.string.plant,
        typeScreen = TypeScreen.Tertiary
    ){
        override val route = "$baseRoute/{gardenId}"

        override fun createRoute(id: Long) = "$baseRoute/$id"
    }
}