package com.habitiora.linkarium.ui.scaffold

import androidx.compose.material3.FabPosition
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@DslMarker
annotation class ScaffoldDsl

@ScaffoldDsl
class WidthScope<T>(
    private val map: MutableMap<WindowWidthSizeClass, T>
) {
    fun compact(value: T) {
        map[WindowWidthSizeClass.Compact] = value
    }

    fun medium(value: T) {
        map[WindowWidthSizeClass.Medium] = value
    }

    fun expanded(value: T) {
        map[WindowWidthSizeClass.Expanded] = value
    }

    fun all(value: T) {
        WindowWidthSizeClass.DefaultSizeClasses.forEach {
            map[it] = value
        }
    }
}

@ScaffoldDsl
class ScaffoldConfigDsl {

    /* -------- Configuración general -------- */

    private var gesturesEnabled: Boolean = true
    private var containerColor: Color = Color.Transparent
    private var contentColor: Color = Color.Unspecified
    private var snackbarHost: (@Composable () -> Unit)? = null

    /* -------- Configuración por tamaño -------- */

    private val topBars =
        mutableMapOf<WindowWidthSizeClass, @Composable () -> Unit>()

    private val bottomBars =
        mutableMapOf<WindowWidthSizeClass, @Composable () -> Unit>()

    private val fabs =
        mutableMapOf<WindowWidthSizeClass, @Composable () -> Unit>()

    private val fabPositions =
        mutableMapOf<WindowWidthSizeClass, FabPosition>()

    private val navigationRails =
        mutableMapOf<WindowWidthSizeClass, @Composable () -> Unit>()

    private var drawer: (@Composable (() -> Unit) -> Unit)? = null

    fun enableGestures(enabled: Boolean) {
        gesturesEnabled = enabled
    }

    fun containerColor(color: Color) {
        containerColor = color
    }

    fun contentColor(color: Color) {
        contentColor = color
    }

    fun snackbarHost(host: @Composable () -> Unit) {
        snackbarHost = host
    }

    fun topBar(block: WidthScope<@Composable () -> Unit>.() -> Unit) {
        WidthScope(topBars).apply(block)
    }

    fun bottomBar(block: WidthScope<@Composable () -> Unit>.() -> Unit) {
        WidthScope(bottomBars).apply(block)
    }

    fun fab(block: WidthScope<@Composable () -> Unit>.() -> Unit) {
        WidthScope(fabs).apply(block)
    }

    fun fabPosition(block: WidthScope<FabPosition>.() -> Unit) {
        WidthScope(fabPositions).apply(block)
    }

    fun navigationRail(
        block: WidthScope<@Composable () -> Unit>.() -> Unit
    ) {
        WidthScope(navigationRails).apply(block)
    }

    fun drawer(
        content: @Composable (closeDrawer: () -> Unit) -> Unit
    ) {
        drawer = content
    }

    fun build(): ScaffoldConfig =
        ScaffoldConfig(
            gesturesEnabled = gesturesEnabled,
            containerColor = containerColor,
            contentColor = contentColor,
            snackbarHost = snackbarHost,
            topBars = topBars.toMap(),
            bottomBars = bottomBars.toMap(),
            fabs = fabs.toMap(),
            fabPositions = fabPositions.toMap(),
            navigationRails = navigationRails.toMap(),
            drawer = drawer
        )
}

@Composable
fun rememberScaffoldConfig(
    vararg keys: Any?,
    block: ScaffoldConfigDsl.() -> Unit
): ScaffoldConfig {
    return remember(*keys) {
        ScaffoldConfigDsl()
            .apply(block)
            .build()
    }
}
