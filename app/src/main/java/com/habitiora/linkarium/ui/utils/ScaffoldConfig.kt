package com.habitiora.linkarium.ui.utils

import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class ScaffoldConfig private constructor(
    val topBarCompact: @Composable () -> Unit,
    val topBarMedium: @Composable () -> Unit,
    val topBarExpanded: @Composable () -> Unit,
    val bottomBarCompact: @Composable () -> Unit,
    val bottomBarMedium: @Composable () -> Unit,
    val bottomBarExpanded: @Composable () -> Unit,
    val snackbarHostState: SnackbarHostState,
    val floatingActionButtonCompact: @Composable () -> Unit,
    val floatingActionButtonMedium: @Composable () -> Unit,
    val floatingActionButtonExpanded: @Composable () -> Unit,
    val floatingActionButtonPositionCompact: FabPosition,
    val floatingActionButtonPositionMedium: FabPosition,
    val floatingActionButtonPositionExpanded: FabPosition,
    val containerColor: Color,
    val contentColor: Color,
    val enabledGesturesInModal: Boolean
) {

    data class Builder(
        private var topBarCompact: @Composable () -> Unit = {},
        private var topBarMedium: @Composable () -> Unit = {},
        private var topBarExpanded: @Composable () -> Unit = {},
        private var bottomBarCompact: @Composable () -> Unit = {},
        private var bottomBarMedium: @Composable () -> Unit = {},
        private var bottomBarExpanded: @Composable () -> Unit = {},
        private var snackbarHostState: SnackbarHostState = SnackbarHostState(),
        private var floatingActionButtonCompact: @Composable () -> Unit = {},
        private var floatingActionButtonMedium: @Composable () -> Unit = {},
        private var floatingActionButtonExpanded: @Composable () -> Unit = {},
        private var floatingActionButtonPositionCompact: FabPosition = FabPosition.End,
        private var floatingActionButtonPositionMedium: FabPosition = FabPosition.End,
        private var floatingActionButtonPositionExpanded: FabPosition = FabPosition.End,
        private var containerColor: Color = Color.Transparent,
        private var contentColor: Color = Color.Unspecified,
        private var enabledGesturesInModal: Boolean = true
    ) {

        fun topBar(
            widthSizeClass: WindowWidthSizeClass,
            topBar: @Composable () -> Unit
        ) = apply {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> topBarCompact = topBar
                WindowWidthSizeClass.Medium -> topBarMedium = topBar
                WindowWidthSizeClass.Expanded -> topBarExpanded = topBar
            }
        }

        fun topBar(topBar: @Composable () -> Unit) = apply {
            this.topBarCompact = topBar
            this.topBarMedium = topBar
            this.topBarExpanded = topBar
        }

        fun bottomBar(
            widthSizeClass: WindowWidthSizeClass,
            bottomBar: @Composable () -> Unit
        ) = apply {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> bottomBarCompact = bottomBar
                WindowWidthSizeClass.Medium -> bottomBarMedium = bottomBar
                WindowWidthSizeClass.Expanded -> bottomBarExpanded = bottomBar
            }
        }

        fun bottomBar(bottomBar: @Composable () -> Unit) = apply {
            this.bottomBarCompact = bottomBar
            this.bottomBarMedium = bottomBar
            this.bottomBarExpanded = bottomBar
        }

        fun snackbarHostState(snackbarHostState: SnackbarHostState) =
            apply { this.snackbarHostState = snackbarHostState }

        fun floatingActionButton(
            widthSizeClass: WindowWidthSizeClass,
            floatingActionButton: @Composable () -> Unit
        ) = apply {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> floatingActionButtonCompact = floatingActionButton
                WindowWidthSizeClass.Medium -> floatingActionButtonMedium = floatingActionButton
                WindowWidthSizeClass.Expanded -> floatingActionButtonExpanded = floatingActionButton
            }
        }

        fun floatingActionButton(floatingActionButton: @Composable () -> Unit) = apply {
            this.floatingActionButtonCompact = floatingActionButton
            this.floatingActionButtonMedium = floatingActionButton
            this.floatingActionButtonExpanded = floatingActionButton
        }

        fun floatingActionButtonPosition(
            widthSizeClass: WindowWidthSizeClass,
            floatingActionButtonPosition: FabPosition
        ) = apply {
            when (widthSizeClass) {
                WindowWidthSizeClass.Compact -> this.floatingActionButtonPositionCompact = floatingActionButtonPosition
                WindowWidthSizeClass.Medium -> this.floatingActionButtonPositionMedium = floatingActionButtonPosition
                WindowWidthSizeClass.Expanded -> this.floatingActionButtonPositionExpanded = floatingActionButtonPosition
            }
        }

        fun floatingActionButtonPosition(floatingActionButtonPosition: FabPosition) = apply {
            this.floatingActionButtonPositionCompact = floatingActionButtonPosition
            this.floatingActionButtonPositionMedium = floatingActionButtonPosition
            this.floatingActionButtonPositionExpanded = floatingActionButtonPosition
        }

        fun containerColor(containerColor: Color) = apply { this.containerColor = containerColor }

        fun contentColor(contentColor: Color) = apply { this.contentColor = contentColor }

        fun enabledGesturesInModal(enabled: Boolean) = apply { this.enabledGesturesInModal = enabled }

        fun build() = ScaffoldConfig(
            topBarCompact = topBarCompact,
            topBarMedium = topBarMedium,
            topBarExpanded = topBarExpanded,
            bottomBarCompact = bottomBarCompact,
            bottomBarMedium = bottomBarMedium,
            bottomBarExpanded = bottomBarExpanded,
            snackbarHostState = snackbarHostState,
            floatingActionButtonCompact = floatingActionButtonCompact,
            floatingActionButtonMedium = floatingActionButtonMedium,
            floatingActionButtonExpanded = floatingActionButtonExpanded,
            floatingActionButtonPositionCompact = floatingActionButtonPositionCompact,
            floatingActionButtonPositionMedium = floatingActionButtonPositionMedium,
            floatingActionButtonPositionExpanded = floatingActionButtonPositionExpanded,
            containerColor = containerColor,
            contentColor = contentColor,
            enabledGesturesInModal = enabledGesturesInModal
        )
    }
}