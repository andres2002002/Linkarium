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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.akari.uicomponents.tooltip.AkariTooltip
import com.habitiora.linkarium.R
import com.habitiora.linkarium.ui.navigation.NavigationHost
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.scaffold.dialogs.DialogApp
import com.habitiora.linkarium.ui.screens.gardenManager.GardenManagerDialog
import com.habitiora.linkarium.ui.utils.navigationEvents.NavigationEvent
import com.habitiora.linkarium.ui.utils.nevControllerFunctions.navigateTo
import timber.log.Timber

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

    val enableAddSeed by viewModel.enableAddSeed.collectAsState()

    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigationEvent.To -> {
                    Timber.d("Navigating to ${event.screen.route}, is top level: ${event.screen.isTopLevel}")
                    navController.navigateTo(screen = event.screen, event.id, event.inclusive)
                }
                NavigationEvent.Back -> { navController.popBackStack() }
            }
        }
    }

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

    val linkariumScaffoldConfig = scaffoldConfig(
        navigateTo = { screen -> viewModel.navigateTo(screen) },
        back = { viewModel.back() },
        snackbarHostState,
        currentScreen,
        menuItems,
        enableAddSeed,
        viewModel::emitEventAddSeed
    )

    message?.let { value -> DialogApp(value, viewModel::dismissDialog) }
    gardenUpdate?.let { _ -> GardenManagerDialog(onDismiss = viewModel::consumeGarden)}



    LinkariumScaffold(
        windowSizeClass = windowSizeClass,
        config = linkariumScaffoldConfig
    ) {
        NavigationHost(navController, windowSizeClass)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun scaffoldConfig(
    navigateTo: (Screens) -> Unit,
    back: () -> Unit,
    snackbarHostState: SnackbarHostState,
    currentScreen: Screens?,
    menuItems: List<Screens>,
    enableAddSeed: Boolean,
    onSaveSeed: () -> Unit
): ScaffoldConfig {
    val colorScheme = MaterialTheme.colorScheme

    return rememberScaffoldConfig(
        currentScreen,
        snackbarHostState,
        enableAddSeed,
        menuItems
    ) {
        val isSettingsScreen = currentScreen is Screens.Settings
        val isTopLevel = currentScreen?.isTopLevel ?: true

        enableGestures(false)
        containerColor(colorScheme.background)
        contentColor(colorScheme.onBackground)
        snackbarHost {
            SnackbarHost(snackbarHostState)
        }
        topBar{
            compact {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                id = currentScreen?.title ?: Screens.ShowGarden.title
                            )
                        )
                    },
                    navigationIcon = {
                        AnimatedVisibility(
                            visible = !isTopLevel,
                            enter = fadeIn(
                                animationSpec = tween(
                                    250,
                                    easing = LinearOutSlowInEasing
                                )
                            ) +
                                    slideInHorizontally(
                                        initialOffsetX = { -it / 2 },
                                        animationSpec = tween(300, easing = {
                                            OvershootInterpolator(1.1f).getInterpolation(it)
                                        })
                                    ),
                            exit = fadeOut(
                                animationSpec = tween(
                                    200,
                                    easing = FastOutLinearInEasing
                                )
                            ) +
                                    slideOutHorizontally(
                                        targetOffsetX = { -it / 3 },
                                        animationSpec = tween(250, easing = FastOutSlowInEasing)
                                    )
                        ) {
                            AkariTooltip(
                                text = "Back",
                            ) {
                                IconButton(
                                    onClick = back
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = isTopLevel,
                        ) {
                            AkariTooltip(
                                text = "Settings",
                            ) {

                                val iconSettings =
                                    if (isSettingsScreen) Icons.Filled.Settings else Icons.Outlined.Settings
                                IconButton(
                                    onClick = { navigateTo(Screens.Settings) },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (isSettingsScreen) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                        else Color.Unspecified
                                    )
                                ) {
                                    Icon(iconSettings, contentDescription = "Settings")
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = currentScreen is Screens.PlantNew
                        ) {
                            AkariTooltip(
                                text = "Save Seed",
                            ) {
                                IconButton(
                                    onClick = onSaveSeed,
                                    enabled = enableAddSeed,
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Filled.Save, contentDescription = "Save Seed")
                                }
                            }
                        }
                    }
                )
            }
        }
        bottomBar {
            compact {
                if (isTopLevel) {
                    NavigationBar {
                        menuItems.forEach { item ->
                            val selected = item.route == currentScreen?.route
                            val text = stringResource(item.title)
                            val iconRes = if (selected) item.iconSelect else item.iconUnselect
                            val icon =
                                ImageVector.vectorResource(iconRes ?: R.drawable.round_home_24)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTo(item) },
                                icon = { Icon(imageVector = icon, contentDescription = text) },
                                label = { Text(text = text) }
                            )
                        }
                    }
                }
            }
        }
        fab {
            compact {
                if ((isTopLevel && !isSettingsScreen) || currentScreen is Screens.ShowSeeds) {
                    AkariTooltip(
                        text = "Add New Seed",
                    ) {
                        FloatingActionButton(
                            onClick = { navigateTo(Screens.PlantNew) }
                        ) {
                            Icon(Icons.Filled.AddLink, contentDescription = "add link")
                        }
                    }
                }
            }
        }
        fabPosition { compact(FabPosition.End) }
        navigationRail {
            medium {
                AppNavigationRail(
                    navigateTo = navigateTo,
                    back = back,
                    currentScreen = currentScreen,
                    menuItems = menuItems,
                    enableAddSeed = enableAddSeed,
                    onSaveSeed = onSaveSeed
                )
            }
            expanded {
                AppNavigationRail(
                    navigateTo = navigateTo,
                    back = back,
                    currentScreen = currentScreen,
                    menuItems = menuItems,
                    enableAddSeed = enableAddSeed,
                    onSaveSeed = onSaveSeed
                )
            }
        }
    }
}

@Composable
private fun AppNavigationRail(
    navigateTo: (Screens) -> Unit,
    back: () -> Unit,
    currentScreen: Screens?,
    menuItems: List<Screens>,
    enableAddSeed: Boolean,
    onSaveSeed: () -> Unit
){
    val isSettingsScreen = currentScreen is Screens.Settings
    val isTopLevel = currentScreen?.isTopLevel ?: true
    NavigationRail(
        header = {
            AnimatedVisibility(
                visible = isTopLevel && !isSettingsScreen
            ) {
                AkariTooltip(
                    text = "Add New Seed",
                ) {
                    FloatingActionButton(
                        onClick = {
                            navigateTo(Screens.PlantNew)
                        }
                    ) {
                        Icon(Icons.Filled.AddLink, contentDescription = "add link")
                    }
                }
            }
            AnimatedVisibility(
                visible = !isTopLevel
            ) {
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    if (currentScreen is Screens.PlantNew) {
                        AkariTooltip(
                            text = "Save Seed",
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    if (enableAddSeed) onSaveSeed()
                                },
                                containerColor = if (enableAddSeed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.7f
                                ),
                                contentColor = if (enableAddSeed) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.7f
                                )
                            ) {
                                Icon(
                                    Icons.Filled.Save,
                                    contentDescription = "Save Seed"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    AkariTooltip(
                        text = "Back",
                    ) {
                        IconButton(
                            onClick = back
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            }
        }
    ) {
        if (!isTopLevel) return@NavigationRail
        menuItems.forEach { item ->
            val selected = item.route == currentScreen?.route
            val text = stringResource(item.title)
            val iconRes = if (selected) item.iconSelect else item.iconUnselect
            val icon = ImageVector.vectorResource(iconRes ?: R.drawable.round_home_24)
            NavigationRailItem(
                selected = selected,
                onClick = { navigateTo(item) },
                icon = { Icon(imageVector = icon, contentDescription = text) },
                label = { Text(text = text) }
            )
        }
        val iconSettings =
            if (isSettingsScreen) Icons.Filled.Settings else Icons.Outlined.Settings
        NavigationRailItem(
            selected = isSettingsScreen,
            onClick = { navigateTo(Screens.Settings) },
            icon = { Icon(iconSettings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }
}