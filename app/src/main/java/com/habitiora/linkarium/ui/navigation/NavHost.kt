package com.habitiora.linkarium.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.habitiora.linkarium.ui.screens.showGarden.ShowGardenScreen

@Composable
fun NavigationHost(
    navController: NavHostController
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
            Text("Plant New")
            //PlantNewScreen()
        }
    }
}