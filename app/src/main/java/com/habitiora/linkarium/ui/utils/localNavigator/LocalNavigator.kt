package com.habitiora.linkarium.ui.utils.localNavigator

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.habitiora.linkarium.ui.navigation.Screens

val LocalNavigator = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

fun NavHostController.navigateSingleTopTo(screen: Screens) {
    this.navigate(screen.route) {
        // Evita duplicados y conserva estado donde es posible
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}