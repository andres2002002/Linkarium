package com.habitiora.linkarium.ui.components.textField

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusProperties
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

data class ProTextFieldState(
    val value: TextFieldValue,
    val onValueChange: (TextFieldValue) -> Unit,
    val label: (@Composable () -> Unit)? = null,
    val placeholder: (@Composable () -> Unit)? = null,
    val prefix: String = "",
    val suffix: String = "",
    val textStyle: TextStyle? = null,
    val readOnly: Boolean = false,
    val requestFocus: Boolean = false,
    val isError: Boolean = false,
    val supportingText: String? = null,
    val keyboardActions: KeyboardActions = KeyboardActions(),
    val keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val leadingIcon: @Composable ((Boolean) -> Unit)? = null,
    val trailingIcon: @Composable ((Boolean) -> Unit)? = null,
    val focusRequester: FocusRequester? = null,
    val focusProperties: FocusProperties.() -> Unit = {},
    val shape: Shape? = null,
    val colors: TextFieldColors? = null
){
    val valueText: String get() = value.text
}