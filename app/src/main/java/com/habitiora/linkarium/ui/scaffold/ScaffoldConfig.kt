package com.habitiora.linkarium.ui.scaffold

import androidx.compose.material3.FabPosition
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController


/**
 * Configuración inmutable del LinkariumScaffold.
 */
@Immutable
class ScaffoldConfig internal constructor(
    val gesturesEnabled: Boolean,
    val containerColor: Color,
    val contentColor: Color,
    val snackbarHost: (@Composable () -> Unit)?,

    private val topBars:
    Map<WindowWidthSizeClass, @Composable () -> Unit>,

    private val bottomBars:
    Map<WindowWidthSizeClass, @Composable () -> Unit>,

    private val fabs:
    Map<WindowWidthSizeClass, @Composable () -> Unit>,

    private val fabPositions:
    Map<WindowWidthSizeClass, FabPosition>,

    private val navigationRails:
    Map<WindowWidthSizeClass, @Composable () -> Unit>,

    val drawer:
    (@Composable (closeDrawer: () -> Unit) -> Unit)?
) {

    /* -------------------- Access API -------------------- */

    /**
     * TopBar correspondiente al ancho actual.
     */
    @Composable
    fun topBar(width: WindowWidthSizeClass) =
        topBars[width]

    /**
     * BottomBar correspondiente al ancho actual.
     */
    @Composable
    fun bottomBar(width: WindowWidthSizeClass) =
        bottomBars[width]

    /**
     * FloatingActionButton correspondiente al ancho actual.
     */
    @Composable
    fun fab(width: WindowWidthSizeClass) =
        fabs[width]

    /**
     * Posición del FAB, con fallback seguro.
     */
    fun fabPosition(width: WindowWidthSizeClass): FabPosition =
        fabPositions[width] ?: FabPosition.End

    /**
     * NavigationRail correspondiente al ancho actual.
     */
    @Composable
    fun navigationRail(width: WindowWidthSizeClass) =
        navigationRails[width]
}