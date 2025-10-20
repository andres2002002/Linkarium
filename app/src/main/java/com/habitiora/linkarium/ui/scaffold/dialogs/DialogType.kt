package com.habitiora.linkarium.ui.scaffold.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DialogType(
    val contentDescription: String?,
    val imageVector: ImageVector,
    val tint: Color
) {
    object Loading : DialogType(null, Icons.Default.Info, Color.Transparent)
    object Error : DialogType("Error", Icons.Default.Error, Color.Red)
    object Confirmation : DialogType("Confirmation", Icons.Default.Info, Color.Blue)
    object Warning : DialogType("Warning", Icons.Rounded.Warning, Color.Yellow)
}