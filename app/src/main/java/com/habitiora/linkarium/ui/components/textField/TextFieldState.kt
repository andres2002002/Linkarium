package com.habitiora.linkarium.ui.components.textField

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

@Stable
class TextFieldState(
    initialValue: TextFieldValue = TextFieldValue(""),
    private val validator: ((String) -> String?)? = null
) {
    var value by mutableStateOf(initialValue)

    var isError by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val text: String get() = value.text

    fun updateValue(newValue: TextFieldValue) {
        value = newValue
        validate()
    }

    fun updateText(newText: String) {
        value = value.copy(text = newText)
        validate()
    }

    private fun validate() {
        validator?.let { validate ->
            errorMessage = validate(value.text)
            isError = errorMessage != null
        }
    }

    fun clearError() {
        isError = false
        errorMessage = null
    }

    fun setError(message: String) {
        isError = true
        errorMessage = message
    }
}