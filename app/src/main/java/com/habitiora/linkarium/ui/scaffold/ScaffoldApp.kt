package com.habitiora.linkarium.ui.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitiora.linkarium.ui.components.buttons.PlainTooltipFAB
import com.habitiora.linkarium.ui.components.buttons.PlainTooltipIconButton
import com.habitiora.linkarium.ui.navigation.NavigationHost
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.navigation.TypeScreen
import com.habitiora.linkarium.ui.utils.localNavigator.navigateSingleTopTo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldApp(
    windowSizeClass: WindowSizeClass,
    viewModel: ScaffoldViewModel = hiltViewModel()
){
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    val currentScreen = remember(currentRoute) { Screens.getScreenByRoute(currentRoute) }

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }


    val menuItems = listOf(
        Screens.ShowGarden,
        Screens.Gardens
    )

    val linkariumScaffoldConfig = scaffoldConfig(navController, snackbarHostState, currentScreen, menuItems)

    LinkariumScaffold(
        windowSizeClass = windowSizeClass,
        navController = navController,
        config = linkariumScaffoldConfig
    ) {
        NavigationHost(navController, windowSizeClass)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun scaffoldConfig(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    currentScreen: Screens?,
    menuItems: List<Screens>
): ScaffoldConfig {
    return ScaffoldConfig.Builder()
        .enableGestures(false)
        .containerColor(MaterialTheme.colorScheme.background)
        .contentColor(MaterialTheme.colorScheme.onBackground)
        .snackbarHost {
            SnackbarHost(snackbarHostState)
        }
        .topBar(WindowWidthSizeClass.Compact){
            TopAppBar(
                title = { Text(text = stringResource(id = currentScreen?.normalTitle ?: Screens.ShowGarden.normalTitle)) },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = currentScreen?.typeScreen != TypeScreen.Primary
                    ) {
                        PlainTooltipIconButton(
                            tooltipText = "Back",
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = currentScreen?.typeScreen == TypeScreen.Primary
                    ) {
                        PlainTooltipIconButton(
                            tooltipText = "Settings",
                            onClick = { navController.navigateSingleTopTo(Screens.Settings) }
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
        .bottomBar(WindowWidthSizeClass.Compact){
            if (currentScreen?.typeScreen != TypeScreen.Tertiary) {
                NavigationBar {
                    menuItems.forEach { item ->
                        val selected = item.route == currentScreen?.route
                        val text = stringResource(item.normalTitle)
                        val icon =
                            ImageVector.vectorResource(if (selected) item.iconSelect else item.iconUnselect)
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateSingleTopTo(item) },
                            icon = { Icon(imageVector = icon, contentDescription = text) },
                            label = { Text(text = text) }
                        )
                    }
                }
            }
        }
        .floatingActionButton(WindowWidthSizeClass.Compact) {
            if (currentScreen?.typeScreen == TypeScreen.Primary) {
                FloatingActionButton(
                    onClick = { navController.navigateSingleTopTo(Screens.PlantNew) },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "add")
                }
            }
        }
        .floatingActionButtonPosition(WindowWidthSizeClass.Compact, FabPosition.End)
        .navigationRail(
            arrayOf(WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded)
        ){ cRoute, navController ->
            NavigationRail(
                header = {
                    AnimatedVisibility(
                        visible = currentScreen?.typeScreen == TypeScreen.Primary
                    ) {
                        PlainTooltipFAB(
                            tooltipText = "Add New Seed",
                            onClick = { navController.navigateSingleTopTo(Screens.PlantNew) }
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "add")
                        }
                    }
                    AnimatedVisibility(
                        visible = currentScreen?.typeScreen == TypeScreen.Tertiary
                    ){
                        PlainTooltipIconButton(
                            tooltipText = "Back",
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            ) {
                if (currentScreen?.typeScreen == TypeScreen.Tertiary) return@NavigationRail
                menuItems.forEach { item ->
                    val selected = item.route == currentScreen?.route
                    val text = stringResource(item.normalTitle)
                    val icon = ImageVector.vectorResource(if (selected) item.iconSelect else item.iconUnselect)
                    NavigationRailItem(
                        selected = selected,
                        onClick = { navController.navigateSingleTopTo(item) },
                        icon = { Icon(imageVector = icon, contentDescription = text) },
                        label = { Text(text = text) }
                    )
                }
                val selected = cRoute == Screens.Settings.route
                val iconSettings = if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
                NavigationRailItem(
                    selected = selected,
                    onClick = { navController.navigateSingleTopTo(Screens.Settings) },
                    icon = { Icon(iconSettings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
        .build()
}

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