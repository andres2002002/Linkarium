package com.habitiora.linkarium.ui.components.textField

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange

@Composable
fun RoundedTextFieldPro(
    modifier: Modifier = Modifier,
    state: ProTextFieldState,
) {
    val focusRequester = state.focusRequester ?: remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }


    // Defaults composables (si vienen nulos en state)
    val colors = state.colors ?: RoundedTextFieldDefaults.colors()
    val shape = state.shape ?: RoundedTextFieldDefaults.shape()
    val textStyle = state.textStyle ?: RoundedTextFieldDefaults.textStyle()

    OutlinedTextField(
        value = state.value,
        onValueChange = state.onValueChange,
        label = state.label,
        placeholder = state.placeholder,
        prefix = state.prefix.takeIf { it.isNotEmpty() }?.let { { Text(text = it) } },
        suffix = state.suffix.takeIf { it.isNotEmpty() }?.let { { Text(text = it) } },
        readOnly = state.readOnly,
        isError = state.isError,
        supportingText = state.supportingText?.let { {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = if (state.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        } },
        keyboardOptions = state.keyboardOptions,
        keyboardActions = state.keyboardActions,
        visualTransformation = state.visualTransformation,
        leadingIcon = state.leadingIcon?.let { { it(isFocused) } } ,
        trailingIcon = state.trailingIcon?.let { { it(isFocused) } } ,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    isFocused = true
                    state.onValueChange(
                        state.value.copy(
                            selection = TextRange(0, state.valueText.length)
                        )
                    )
                } else {
                    isFocused = false
                }
            }
            .focusProperties(scope = state.focusProperties),
        colors = colors,
        shape = shape,
        textStyle = textStyle
    )

    LaunchedEffect(state.requestFocus) {
        if (!state.readOnly && state.requestFocus) {
            focusRequester.requestFocus()
        }
    }
}