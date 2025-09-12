package com.habitiora.linkarium.ui.components.textField

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object RoundedTextFieldDefaults{
    @Composable
    fun colors() = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
        unfocusedContainerColor = Color.Unspecified,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedLeadingIconColor = Color.Unspecified,
        errorLeadingIconColor = MaterialTheme.colorScheme.error,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = Color.Unspecified,
        errorTrailingIconColor = MaterialTheme.colorScheme.error,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        errorTextColor = MaterialTheme.colorScheme.error
    )

    @Composable
    fun dynamicColorsBg(backgroundColor: Color) = colors().copy(
        unfocusedContainerColor = backgroundColor.copy(alpha = 0.7f),
        unfocusedLeadingIconColor = Color.Unspecified,
        unfocusedTrailingIconColor = Color.Unspecified,
    )

    @Composable
    fun shape() = MaterialTheme.shapes.large

    @Composable
    fun textStyle() = MaterialTheme.typography.bodyMedium
}
