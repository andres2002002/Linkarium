package com.habitiora.linkarium.ui.components.textField

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class TextFieldContent(
    val label: String? = null,
    val placeholder: String? = null,
    val prefix: (@Composable () -> Unit)? = null,
    val suffix: (@Composable () -> Unit)? = null,
    val leadingIcon: (@Composable (focused: Boolean, isError: Boolean) -> Unit)? = null,
    val trailingIcon: (@Composable (focused: Boolean, isError: Boolean) -> Unit)? = null,
    val supportingText: String? = null
) {
    companion object {
        fun withLabel(text: String) = TextFieldContent(label = text)
        fun withPlaceholder(text: String) = TextFieldContent(placeholder = text)
        fun withLabelAndPlaceholder(label: String, placeholder: String) =
            TextFieldContent(label = label, placeholder = placeholder)
        fun withStringPrefix(text: String) = TextFieldContent(prefix = { Text(text) })
        fun withStringSuffix(text: String) = TextFieldContent(suffix = { Text(text) })
        fun withSupportingText(text: String) = TextFieldContent(supportingText = text)
    }
}