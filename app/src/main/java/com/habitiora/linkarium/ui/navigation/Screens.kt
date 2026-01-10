package com.habitiora.linkarium.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.habitiora.linkarium.R

sealed class Screens(
    val baseRoute: String,
    @StringRes val title: Int,
    @DrawableRes val iconSelect: Int? = null,
    @DrawableRes val iconUnselect: Int? = iconSelect,
    val isTopLevel: Boolean = false
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
        title = R.string.link_garden,
        iconSelect = R.drawable.round_home_24,
        iconUnselect = R.drawable.outline_home_24,
        isTopLevel = true
    )
    data object Gardens: Screens(
        baseRoute = "gardens",
        title = R.string.gardens,
        iconSelect = R.drawable.round_collections_bookmark_24,
        iconUnselect = R.drawable.outline_collections_bookmark_24,
        isTopLevel = true
    )
    data object Settings: Screens(
        baseRoute = "settings",
        title = R.string.settings,
        isTopLevel = true
    )
    data object PlantNew: Screens(
        baseRoute = "plant_new",
        title = R.string.plant,
    ){
        override val route = "$baseRoute?seedId={seedId}"

        override fun createRoute(id: Long): String {
            return if (id > 0) "$baseRoute?seedId=$id"
            else baseRoute
        }
    }

    data object ShowSeeds: Screens(
        baseRoute = "show_seeds",
        title = R.string.plant,
    ){
        override val route = "$baseRoute/{gardenId}"

        override fun createRoute(id: Long) = "$baseRoute/$id"
    }
}