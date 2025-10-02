package com.habitiora.linkarium.ui.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitiora.linkarium.ui.navigation.NavigationHost
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.scaffold.ScaffoldConfig
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

    val menuItems = listOf(
        Screens.ShowGarden,
        Screens.Gardens
    )

    val scaffoldConfig = ScaffoldConfig.Builder()
        .enabledGesturesInModal(false)
        .containerColor(MaterialTheme.colorScheme.background)
        .contentColor(MaterialTheme.colorScheme.onBackground)
        .topBar(WindowWidthSizeClass.Compact){
            TopAppBar(
                title = { Text(text = stringResource(id = Screens.ShowGarden.normalTitle)) },
                actions = {
                    IconButton(onClick = { navController.navigateSingleTopTo(Screens.Settings) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
        .bottomBar(WindowWidthSizeClass.Compact){
            NavigationBar {
                menuItems.forEach { item ->
                    val selected = item.route == currentRoute
                    val text = stringResource(item.normalTitle)
                    val icon = ImageVector.vectorResource(if (selected) item.iconSelect else item.iconUnselect)
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigateSingleTopTo(item) },
                        icon = { Icon(imageVector = icon, contentDescription = text) },
                        label = { Text(text = text) }
                    )
                }
            }
        }
        .floatingActionButton(WindowWidthSizeClass.Compact) {
            FloatingActionButton(
                onClick = { navController.navigateSingleTopTo(Screens.PlantNew) },
            ) {
                Icon(Icons.Filled.Add, contentDescription = "add")
            }
        }
        .floatingActionButtonPosition(WindowWidthSizeClass.Compact, FabPosition.End)

    ScaffoldLinkarium(
        windowSizeClass = windowSizeClass,
        navController = navController,
        config = scaffoldConfig.build(),
        content = { NavigationHost(navController, windowSizeClass)}
    )
}
@Composable
fun ScaffoldLinkarium(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController,
    config: ScaffoldConfig,
    content: @Composable () -> Unit
){
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CompactScaffold(
                currentRoute = currentRoute,
                config = config,
                onNavigate = navController::navigateSingleTopTo,
                content = content
            )
        }
        WindowWidthSizeClass.Medium -> {
            MediumScaffold(
                currentRoute = currentRoute,
                config = config,
                onNavigate = navController::navigateSingleTopTo,
                content = content
            )
        }
        WindowWidthSizeClass.Expanded -> {
            ExpandedScaffold(
                currentRoute = currentRoute,
                config = config,
                onNavigate = navController::navigateSingleTopTo,
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactScaffold(
    currentRoute: String?,
    config: ScaffoldConfig,
    onNavigate: (Screens) -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val menuItems = listOf(
        Screens.ShowGarden,
        Screens.Gardens,
        Screens.Settings
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = config.enabledGesturesInModal,
        drawerContent = {
            ModalDrawerSheet {
                menuItems.forEach { item ->
                    val selected = item.route == currentRoute
                    val text = stringResource(item.normalTitle)
                    val icon = ImageVector.vectorResource(if (selected) item.iconSelect else item.iconUnselect)
                    NavigationDrawerItem(
                        label = { Text(text = text) },
                        selected = selected,
                        onClick = {
                            onNavigate(item)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(imageVector = icon, contentDescription = text) }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = { config.topBarCompact() },
            bottomBar = { config.bottomBarCompact() },
            snackbarHost = { config.snackbarHostState },
            floatingActionButton = { config.floatingActionButtonCompact() },
            floatingActionButtonPosition = config.floatingActionButtonPositionCompact,
            containerColor = config.containerColor,
            contentColor = config.contentColor
        ) { padding ->
            Box(modifier = Modifier.padding(padding)){
                content()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediumScaffold(
    currentRoute: String?,
    config: ScaffoldConfig,
    onNavigate: (Screens) -> Unit,
    content: @Composable () -> Unit
) {
    val menuItems = listOf(
        Screens.ShowGarden,
        Screens.Gardens,
        Screens.Settings
    )

    Scaffold(
        topBar = { config.topBarMedium() },
        bottomBar = { config.bottomBarMedium() },
        snackbarHost = { config.snackbarHostState },
        floatingActionButton = { config.floatingActionButtonMedium() },
        floatingActionButtonPosition = config.floatingActionButtonPositionMedium,
        containerColor = config.containerColor,
        contentColor = config.contentColor
    ) { padding ->
        Row(modifier = Modifier.padding(padding)) {
            NavigationRail {
                menuItems.forEach { item ->
                    val selected = item.route == currentRoute
                    val text = stringResource(item.normalTitle)
                    val icon = ImageVector.vectorResource(if (selected) item.iconSelect else item.iconUnselect)
                    NavigationRailItem(
                        selected = item.route == currentRoute,
                        onClick = { onNavigate(item) },
                        icon = { Icon(imageVector = icon, contentDescription = text) },
                        label = { Text(text = text) }
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)){
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedScaffold(
    currentRoute: String?,
    config: ScaffoldConfig,
    onNavigate: (Screens) -> Unit,
    content: @Composable () -> Unit
) {
    val menuItems = listOf(
        Screens.ShowGarden,
        Screens.Gardens,
        Screens.Settings
    )

    Scaffold(
        topBar = { config.topBarExpanded() },
        bottomBar = { config.bottomBarExpanded() },
        snackbarHost = { config.snackbarHostState },
        floatingActionButton = { config.floatingActionButtonExpanded() },
        floatingActionButtonPosition = config.floatingActionButtonPositionExpanded,
        containerColor = config.containerColor,
        contentColor = config.contentColor
    ) { padding ->
        Row(modifier = Modifier.padding(padding)) {
            NavigationRail {
                menuItems.forEach { item ->
                    val selected = item.route == currentRoute
                    val text = stringResource(item.normalTitle)
                    val icon = ImageVector.vectorResource(if (selected) item.iconSelect else item.iconUnselect)
                    NavigationRailItem(
                        selected = item.route == currentRoute,
                        onClick = { onNavigate(item) },
                        icon = { Icon(imageVector = icon, contentDescription = text) },
                        label = { Text(text = text) }
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    }
}
