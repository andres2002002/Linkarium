package com.habitiora.linkarium.ui.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@Composable
fun LinkariumScaffold(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    config: ScaffoldConfig,
    content: @Composable () -> Unit
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val scaffoldState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            config.topBar[windowSizeClass.widthSizeClass]?.invoke()
        },
        bottomBar = {
            config.bottomBar[windowSizeClass.widthSizeClass]?.invoke()
        },
        floatingActionButton = {
            config.fab[windowSizeClass.widthSizeClass]?.invoke()
        },
        floatingActionButtonPosition =
            config.fabPosition[windowSizeClass.widthSizeClass] ?: FabPosition.End,
        snackbarHost = { config.snackbarHost?.invoke() },
        containerColor = config.containerColor,
        contentColor = config.contentColor
    ) { padding ->
        Row(modifier = Modifier.padding(padding)) {
            // Rail opcional
            config.navigationRail[windowSizeClass.widthSizeClass]?.invoke(currentRoute, navController)

            // Drawer opcional (solo si Compact, por ejemplo)
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact &&
                config.drawer != null
            ) {
                ModalNavigationDrawer(
                    drawerState = scaffoldState,
                    gesturesEnabled = config.gesturesEnabled,
                    drawerContent = {
                        config.drawer.invoke(currentRoute, navController) {
                            scope.launch { scaffoldState.close() }
                        }
                    }
                ) {
                    Box(Modifier.weight(1f)) { content() }
                }
            } else {
                Box(Modifier.weight(1f)) { content() }
            }
        }
    }
}