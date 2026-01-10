package com.habitiora.linkarium.ui.utils.nevControllerFunctions

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.habitiora.linkarium.ui.navigation.Screens

fun NavController.navigateTo(
    screen: Screens,
    id: Long = -1,
    inclusive: Boolean = false
) {
    val route = screen.createRoute(id)

    navigate(route) {
        when {
            screen.isTopLevel -> {
                // Patrón correcto para BottomNav / Rail
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                restoreState = true
            }

            inclusive -> {
                // Limpia el stack (ej: logout, wizard final)
                popUpTo(graph.findStartDestination().id) {
                    this.inclusive = true
                }
            }
        }

        launchSingleTop = true
    }
}
