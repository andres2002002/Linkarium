package com.habitiora.linkarium.ui.scaffold

import androidx.compose.material3.FabPosition
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

class ScaffoldConfig private constructor(
    val gesturesEnabled: Boolean = true,
    val containerColor: Color = Color.Transparent,
    val contentColor: Color = Color.Unspecified,
    val snackbarHost: (@Composable () -> Unit)? = null,
    internal val topBar: Map<WindowWidthSizeClass, @Composable () -> Unit> = emptyMap(),
    internal val bottomBar: Map<WindowWidthSizeClass, @Composable () -> Unit> = emptyMap(),
    internal val fab: Map<WindowWidthSizeClass, @Composable () -> Unit> = emptyMap(),
    internal val fabPosition: Map<WindowWidthSizeClass, FabPosition> = emptyMap(),
    internal val navigationRail: Map<WindowWidthSizeClass, @Composable (String?, NavHostController) -> Unit> = emptyMap(),
    internal val drawer: (@Composable (String?, NavHostController, () -> Unit) -> Unit)? = null
) {

    class Builder {
        private var gesturesEnabled: Boolean = true
        private var containerColor: Color = Color.Transparent
        private var contentColor: Color = Color.Unspecified
        private var snackbarHost: (@Composable () -> Unit)? = null
        private val topBar = mutableMapOf<WindowWidthSizeClass, @Composable () -> Unit>()
        private val bottomBar = mutableMapOf<WindowWidthSizeClass, @Composable () -> Unit>()
        private val fab = mutableMapOf<WindowWidthSizeClass, @Composable () -> Unit>()
        private val fabPosition = mutableMapOf<WindowWidthSizeClass, FabPosition>()
        private val navigationRail = mutableMapOf<WindowWidthSizeClass, @Composable (String?, NavHostController) -> Unit>()
        private var drawer: (@Composable (String?, NavHostController, () -> Unit) -> Unit)? = null

        fun enableGestures(enabled: Boolean) = apply { gesturesEnabled = enabled }

        fun containerColor(color: Color) = apply { containerColor = color }

        fun contentColor(color: Color) = apply { contentColor = color }

        fun snackbarHost(host: @Composable () -> Unit) = apply { snackbarHost = host }

        fun topBar(content: @Composable () -> Unit) = apply {
            topBar[WindowWidthSizeClass.Compact] = content
            topBar[WindowWidthSizeClass.Medium] = content
            topBar[WindowWidthSizeClass.Expanded] = content
        }

        fun topBar(widthSizeClasses: Array<WindowWidthSizeClass>, content: @Composable () -> Unit) = apply {
            widthSizeClasses.forEach { widthSizeClass ->
                topBar(widthSizeClass, content)
            }
        }
        fun topBar(widthSizeClass: WindowWidthSizeClass, content: @Composable () -> Unit) = apply {
            topBar[widthSizeClass] = content
        }

        fun bottomBar(content: @Composable () -> Unit) = apply {
            bottomBar[WindowWidthSizeClass.Compact] = content
            bottomBar[WindowWidthSizeClass.Medium] = content
            bottomBar[WindowWidthSizeClass.Expanded] = content
        }

        fun bottomBar(widthSizeClasses: Array<WindowWidthSizeClass>, content: @Composable () -> Unit) = apply {
            widthSizeClasses.forEach { widthSizeClass ->
                bottomBar(widthSizeClass, content)
            }
        }

        fun bottomBar(widthSizeClass: WindowWidthSizeClass, content: @Composable () -> Unit) = apply {
            bottomBar[widthSizeClass] = content
        }

        fun floatingActionButton(content: @Composable () -> Unit) = apply {
            fab[WindowWidthSizeClass.Compact] = content
            fab[WindowWidthSizeClass.Medium] = content
            fab[WindowWidthSizeClass.Expanded] = content
        }

        fun floatingActionButton(widthSizeClasses: Array<WindowWidthSizeClass>, content: @Composable () -> Unit) = apply {
            widthSizeClasses.forEach { widthSizeClass ->
                floatingActionButton(widthSizeClass, content)
            }
        }

        fun floatingActionButton(
            widthSizeClass: WindowWidthSizeClass,
            content: @Composable () -> Unit
        ) = apply {
            fab[widthSizeClass] = content
        }

        fun floatingActionButtonPosition(
            position: FabPosition
        ) = apply {
            fabPosition[WindowWidthSizeClass.Compact] = position
            fabPosition[WindowWidthSizeClass.Medium] = position
            fabPosition[WindowWidthSizeClass.Expanded] = position
        }

        fun floatingActionButtonPosition(
            widthSizeClasses: Array<WindowWidthSizeClass>,
            position: FabPosition
        ) = apply {
            widthSizeClasses.forEach { widthSizeClass ->
                floatingActionButtonPosition(widthSizeClass, position)
            }
        }

        fun floatingActionButtonPosition(
            widthSizeClass: WindowWidthSizeClass,
            position: FabPosition
        ) = apply {
            fabPosition[widthSizeClass] = position
        }

        fun navigationRail(content: @Composable (String?, NavHostController) -> Unit) = apply {
            navigationRail[WindowWidthSizeClass.Compact] = content
            navigationRail[WindowWidthSizeClass.Medium] = content
            navigationRail[WindowWidthSizeClass.Expanded] = content
        }

        fun navigationRail(widthSizeClasses: Array<WindowWidthSizeClass>, content: @Composable (String?, NavHostController) -> Unit) = apply {
            widthSizeClasses.forEach { widthSizeClass ->
                navigationRail(widthSizeClass, content)
            }
        }

        fun navigationRail(
            widthSizeClass: WindowWidthSizeClass,
            content: @Composable (String?, NavHostController) -> Unit
        ) = apply {
            navigationRail[widthSizeClass] = content
        }

        fun drawer(content: @Composable (String?, NavHostController, () -> Unit) -> Unit) = apply {
            drawer = content
        }

        fun build() = ScaffoldConfig(
            gesturesEnabled = gesturesEnabled,
            containerColor = containerColor,
            contentColor = contentColor,
            snackbarHost = snackbarHost,
            topBar = topBar.toMap(),
            bottomBar = bottomBar.toMap(),
            fab = fab.toMap(),
            fabPosition = fabPosition.toMap(),
            navigationRail = navigationRail.toMap(),
            drawer = drawer
        )
    }
}
