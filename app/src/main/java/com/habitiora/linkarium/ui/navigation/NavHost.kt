package com.habitiora.linkarium.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Box(modifier){
        NavHost(
            navController = navController,
            startDestination = Screens.ShowGarden.route
        ) {
            composable(route = Screens.ShowGarden.route) {
                Text("Show Garden")
                //ShowGardenScreen()
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
}