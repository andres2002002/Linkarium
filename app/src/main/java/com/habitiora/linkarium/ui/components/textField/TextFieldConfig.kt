package com.habitiora.linkarium.ui.components.textField


import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Immutable
import androidx.compose.ui.focus.FocusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Immutable
data class TextFieldConfig private constructor(
    val colors: TextFieldColors? = null,
    val shape: Shape? = null,
    val textStyle: TextStyle? = null,
    val keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    val keyboardActions: KeyboardActions = KeyboardActions.Default,
    val visualTransformation: VisualTransformation = VisualTransformation.None,
    val focusProperties: FocusProperties.() -> Unit = {},
    val autoSelectOnFocus: Boolean = true,
    val enabled: Boolean = true,
    val singleLine: Boolean = false,
    val maxLines: Int = Int.MAX_VALUE,
    val minLines: Int = 1,
    val cursorBrush: Brush? = null
) {
    class Builder {
        private var colors: TextFieldColors? = null
        private var shape: Shape? = null
        private var textStyle: TextStyle? = null
        private var keyboardOptions: KeyboardOptions = KeyboardOptions.Default
        private var keyboardActions: KeyboardActions = KeyboardActions.Default
        private var visualTransformation: VisualTransformation = VisualTransformation.None
        private var focusProperties: FocusProperties.() -> Unit = {}
        private var autoSelectOnFocus: Boolean = true
        private var enabled: Boolean = true
        private var singleLine: Boolean = false
        private var maxLines: Int = Int.MAX_VALUE
        private var minLines: Int = 1
        private var cursorBrush: Brush? = null

        fun colors(colors: TextFieldColors) = apply { this.colors = colors }
        fun shape(shape: Shape) = apply { this.shape = shape }
        fun textStyle(style: TextStyle) = apply { this.textStyle = style }
        fun keyboardOptions(options: KeyboardOptions) = apply { this.keyboardOptions = options }
        fun keyboardActions(actions: KeyboardActions) = apply { this.keyboardActions = actions }
        fun visualTransformation(transform: VisualTransformation) = apply { this.visualTransformation = transform }
        fun focusProperties(properties: FocusProperties.() -> Unit) = apply { this.focusProperties = properties }
        fun autoSelectOnFocus(autoSelect: Boolean) = apply { this.autoSelectOnFocus = autoSelect }
        fun enabled(enabled: Boolean) = apply { this.enabled = enabled }
        fun singleLine(singleLine: Boolean) = apply { this.singleLine = singleLine }
        fun maxLines(maxLines: Int) = apply { this.maxLines = maxLines }
        fun minLines(minLines: Int) = apply { this.minLines = minLines }
        fun cursorBrush(brush: Brush) = apply { this.cursorBrush = brush }

        // Métodos de conveniencia
        fun password() = apply {
            this.visualTransformation = PasswordVisualTransformation()
            this.keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        }

        fun email() = apply {
            this.keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        }

        fun number() = apply {
            this.keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        }

        fun multiline(maxLines: Int = Int.MAX_VALUE, minLines: Int = 3) = apply {
            this.singleLine = false
            this.maxLines = maxLines
            this.minLines = minLines
        }

        fun build() = TextFieldConfig(
            colors, shape, textStyle, keyboardOptions, keyboardActions,
            visualTransformation, focusProperties, autoSelectOnFocus, enabled, singleLine,
            maxLines, minLines, cursorBrush
        )
    }

    companion object {
        fun builder() = Builder()
        val Default = Builder().build()
    }
}