package com.habitiora.linkarium.ui.scaffold

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akari.uicomponents.buttons.AkariButtonVariant
import com.akari.uicomponents.buttons.AkariFabVariant
import com.akari.uicomponents.buttons.tooltipButtons.AkariTooltipButton
import com.akari.uicomponents.buttons.tooltipButtons.AkariTooltipFab
import com.habitiora.linkarium.R
import com.habitiora.linkarium.ui.navigation.NavigationHost
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.navigation.TypeScreen
import com.habitiora.linkarium.ui.scaffold.dialogs.DialogApp
import com.habitiora.linkarium.ui.screens.gardenManager.GardenManagerDialog
import com.habitiora.linkarium.ui.utils.localNavigator.navigateToRoute
import com.habitiora.linkarium.ui.utils.localNavigator.navigateToScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldApp(
    windowSizeClass: WindowSizeClass,
    viewModel: ScaffoldViewModel = hiltViewModel()
){
    val message by viewModel.message.collectAsState()
    val gardenUpdate by viewModel.gardenUpdate.collectAsState()

    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route
    val currentScreen = remember(currentRoute) { Screens.fromRoute(currentRoute) }

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvents.collect { event ->
            snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = event.duration
            )
        }
    }

    val menuItems = listOf(
        Screens.ShowGarden,
        Screens.Gardens
    )

    val linkariumScaffoldConfig = scaffoldConfig(navController, snackbarHostState, currentScreen, menuItems)

    message?.let { value -> DialogApp(value, viewModel::dismissDialog) }
    gardenUpdate?.let { _ -> GardenManagerDialog(onDismiss = viewModel::consumeGarden)}



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
                        visible = currentScreen?.typeScreen != TypeScreen.Primary,
                        enter = fadeIn(animationSpec = tween(250, easing = LinearOutSlowInEasing)) +
                                slideInHorizontally(
                                    initialOffsetX = { -it / 2 },
                                    animationSpec = tween(300, easing = {
                                        OvershootInterpolator(1.1f).getInterpolation(it)
                                    })
                                ),
                        exit = fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing)) +
                                slideOutHorizontally(
                                    targetOffsetX = { -it / 3 },
                                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                                )
                    ) {
                        AkariTooltipButton(
                            variant = AkariButtonVariant.Icon,
                            onClick = { navController.popBackStack() },
                            tooltipContent = {
                                Text(text = "Back")
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = currentScreen?.typeScreen == TypeScreen.Primary
                    ) {
                        AkariTooltipButton(
                            variant = AkariButtonVariant.Icon,
                            onClick = { navController.navigateToScreen(Screens.Settings) },
                            tooltipContent = {
                                Text(text = "Settings")
                            }
                        ){
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
                        val iconRes = if (selected) item.iconSelect else item.iconUnselect
                        val icon = ImageVector.vectorResource(iconRes?: R.drawable.round_home_24)
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateToScreen(item) },
                            icon = { Icon(imageVector = icon, contentDescription = text) },
                            label = { Text(text = text) }
                        )
                    }
                }
            }
        }
        .floatingActionButton(WindowWidthSizeClass.Compact) {
            if (currentScreen?.typeScreen == TypeScreen.Primary) {
                AkariTooltipFab(
                    variant = AkariFabVariant.Normal,
                    onClick = { navController.navigateToRoute(Screens.PlantNew.createRoute()) },
                    tooltipContent = {
                        Text(text = "Add New Seed")
                    }
                ) {
                    Icon(Icons.Filled.AddLink, contentDescription = "add link")
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
                        AkariTooltipFab(
                            variant = AkariFabVariant.Normal,
                            onClick = { navController.navigateToRoute(Screens.PlantNew.createRoute()) },
                            tooltipContent = {
                                Text(text = "Add New Seed")
                            }
                        ) {
                            Icon(Icons.Filled.AddLink, contentDescription = "add link")
                        }
                    }
                    AnimatedVisibility(
                        visible = currentScreen?.typeScreen == TypeScreen.Tertiary
                    ){
                        AkariTooltipButton(
                            variant = AkariButtonVariant.Icon,
                            onClick = { navController.popBackStack() },
                            tooltipContent = {
                                Text(text = "Back")
                            }
                        ){
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            ) {
                if (currentScreen?.typeScreen == TypeScreen.Tertiary) return@NavigationRail
                menuItems.forEach { item ->
                    val selected = item.route == currentScreen?.route
                    val text = stringResource(item.normalTitle)
                    val iconRes = if (selected) item.iconSelect else item.iconUnselect
                    val icon = ImageVector.vectorResource(iconRes?: R.drawable.round_home_24)
                    NavigationRailItem(
                        selected = selected,
                        onClick = { navController.navigateToScreen(item) },
                        icon = { Icon(imageVector = icon, contentDescription = text) },
                        label = { Text(text = text) }
                    )
                }
                val selected = cRoute == Screens.Settings.route
                val iconSettings = if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
                NavigationRailItem(
                    selected = selected,
                    onClick = { navController.navigateToScreen(Screens.Settings) },
                    icon = { Icon(iconSettings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
        .build()
}