package com.habitiora.linkarium.ui.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun LinkariumScaffold(
    windowSizeClass: WindowSizeClass,
    config: ScaffoldConfig,
    content: @Composable () -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val scaffoldContent: @Composable () -> Unit = {
        Scaffold(
            topBar = {
                config.topBar(windowSizeClass.widthSizeClass)?.invoke()
            },
            bottomBar = {
                config.bottomBar(windowSizeClass.widthSizeClass)?.invoke()
            },
            floatingActionButton = {
                config.fab(windowSizeClass.widthSizeClass)?.invoke()
            },
            floatingActionButtonPosition =
                config.fabPosition(windowSizeClass.widthSizeClass),
            snackbarHost = { config.snackbarHost?.invoke() },
            containerColor = config.containerColor,
            contentColor = config.contentColor
        ) { padding ->
            Row(Modifier.padding(padding)) {
                config.navigationRail(windowSizeClass.widthSizeClass)?.invoke()
                Box(Modifier.weight(1f)) {
                    content()
                }
            }
        }
    }

    if (
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact &&
        config.drawer != null
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = config.gesturesEnabled,
            drawerContent = {
                config.drawer.invoke {
                    scope.launch { drawerState.close() }
                }
            }
        ) {
            scaffoldContent()
        }
    } else {
        scaffoldContent()
    }
}