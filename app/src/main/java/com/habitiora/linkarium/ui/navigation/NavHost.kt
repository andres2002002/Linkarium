package com.habitiora.linkarium.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.habitiora.linkarium.ui.screens.plantSeed.PlantSeedScreen
import com.habitiora.linkarium.ui.screens.showGarden.ShowGardenScreen
import com.habitiora.linkarium.ui.utils.localNavigator.LocalNavigator
import com.habitiora.linkarium.ui.utils.localWindowSizeClass.LocalWindowSizeClass

@Composable
fun NavigationHost(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
    CompositionLocalProvider(
        LocalNavigator provides navController,
        LocalWindowSizeClass provides windowSizeClass
    ) {
        NavHost(
            navController = navController,
            startDestination = Screens.ShowGarden.route
        ) {
            composable(route = Screens.ShowGarden.route) {
                ShowGardenScreen()
            }
            composable(route = Screens.Gardens.route) {
                Text("Gardens")
                //GardensScreen()
            }
            composable(route = Screens.Settings.route) {
                Text("Settings")
                //SettingsScreen()
            }
            composable(route = Screens.PlantNew.route) {
                PlantSeedScreen {
                    navController.popBackStack()
                }
            }
        }
    }
}