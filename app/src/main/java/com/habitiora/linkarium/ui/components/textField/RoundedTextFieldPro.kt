package com.habitiora.linkarium.ui.components.textField

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp

@Composable
fun RoundedTextFieldPro2(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    content: TextFieldContent = TextFieldContent(),
    config: TextFieldConfig = TextFieldConfig.Default,
    readOnly: Boolean = false,
    focusRequester: FocusRequester? = null
) {
    val currentFocusRequester = focusRequester ?: remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    // Aplicar defaults si no se proporcionan
    val colors = config.colors ?: RoundedTextFieldDefaults.colors()
    val shape: Shape = config.shape ?: RoundedTextFieldDefaults.shape()
    val textStyle = config.textStyle ?: RoundedTextFieldDefaults.textStyle()
    val cursorBrush = config.cursorBrush ?: SolidColor(
        if (colors.cursorColor != Color.Unspecified) colors.cursorColor
        else MaterialTheme.colorScheme.primary
    )

    Column {
        // Campo principal
        Box(
            modifier = modifier
                .background(
                    color = when {
                        state.isError -> colors.errorContainerColor
                        !config.enabled -> colors.disabledContainerColor
                        isFocused -> colors.focusedContainerColor
                        else -> colors.unfocusedContainerColor
                    },
                    shape = shape
                )
                .border(
                    width = if (isFocused) RoundedTextFieldDefaults.focusedBorderThickness()
                    else RoundedTextFieldDefaults.unfocusedBorderThickness(),
                    color = when {
                        state.isError -> colors.errorIndicatorColor
                        !config.enabled -> colors.disabledIndicatorColor
                        isFocused -> colors.focusedIndicatorColor
                        else -> colors.unfocusedIndicatorColor
                    },
                    shape = shape
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon
                content.leadingIcon?.let { icon ->
                    icon(isFocused, state.isError)
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Prefix
                content.prefix?.let { prefix ->
                    prefix()
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Text Field Container
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    // Label flotante
                    content.label?.let { labelText ->
                        val labelColor = when {
                            state.isError -> colors.errorLabelColor
                            !config.enabled -> colors.disabledLabelColor
                            isFocused -> colors.focusedLabelColor
                            else -> colors.unfocusedLabelColor
                        }

                        val shouldFloat = isFocused || state.text.isNotEmpty()

                        this@Row.AnimatedVisibility(
                            visible = shouldFloat,
                            enter = fadeIn() + slideInVertically { -it / 2 },
                            exit = fadeOut() + slideOutVertically { -it / 2 }
                        ) {
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.bodySmall,
                                color = labelColor,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .offset(y = (-8).dp)
                            )
                        }
                    }

                    // BasicTextField
                    BasicTextField(
                        value = state.value,
                        onValueChange = state::updateValue,
                        enabled = config.enabled && !readOnly,
                        readOnly = readOnly,
                        textStyle = textStyle.copy(
                            color = when {
                                state.isError -> colors.errorTextColor
                                !config.enabled -> colors.disabledTextColor
                                isFocused -> colors.focusedTextColor
                                else -> colors.unfocusedTextColor
                            }
                        ),
                        keyboardOptions = config.keyboardOptions,
                        keyboardActions = config.keyboardActions,
                        singleLine = config.singleLine,
                        maxLines = config.maxLines,
                        minLines = config.minLines,
                        visualTransformation = config.visualTransformation,
                        cursorBrush = cursorBrush,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(currentFocusRequester)
                            .onFocusChanged { focusState ->
                                val wasFocused = isFocused
                                isFocused = focusState.isFocused

                                if (focusState.isFocused && !wasFocused && config.autoSelectOnFocus) {
                                    state.value = state.value.copy(
                                        selection = TextRange(0, state.text.length)
                                    )
                                }
                            }
                            .focusProperties(config.focusProperties),
                        decorationBox = { innerTextField ->
                            Box {
                                // Placeholder
                                if (state.text.isEmpty() && content.placeholder != null) {
                                    Text(
                                        text = content.placeholder,
                                        style = textStyle,
                                        color = when{
                                            state.isError -> colors.errorPlaceholderColor
                                            !config.enabled -> colors.disabledPlaceholderColor
                                            isFocused -> colors.focusedPlaceholderColor
                                            else -> colors.unfocusedPlaceholderColor
                                        },
                                        maxLines = if (config.singleLine) 1 else config.maxLines
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                // Suffix
                content.suffix?.let { suffix ->
                    Spacer(modifier = Modifier.width(4.dp))
                    suffix()
                }

                // Trailing Icon
                content.trailingIcon?.let { icon ->
                    Spacer(modifier = Modifier.width(8.dp))
                    icon(isFocused, state.isError)
                }
            }
        }

        // Supporting Text
        content.supportingText?.let { supportingText ->
            val message = if (state.isError) state.errorMessage ?: supportingText else supportingText
            val textColor = when {
                state.isError -> colors.errorSupportingTextColor
                !config.enabled -> colors.disabledSupportingTextColor
                isFocused -> colors.focusedSupportingTextColor
                else -> colors.unfocusedSupportingTextColor
            }

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxWidth()
            )
        }
    }
}
