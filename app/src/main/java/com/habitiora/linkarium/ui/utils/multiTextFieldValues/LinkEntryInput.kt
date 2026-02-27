package com.habitiora.linkarium.ui.utils.multiTextFieldValues

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue

@Immutable
data class LinkEntryInput(
    val label: TextFieldValue = TextFieldValue(""),
    val url: TextFieldValue = TextFieldValue(""),
    val note: TextFieldValue = TextFieldValue("")
) : FormInputState<LinkEntryInput.Key, LinkEntryInput> {

    enum class Key {
        URL,
        LABEL,
        NOTE
    }

    override fun getValue(key: Key): TextFieldValue = when (key) {
        Key.URL -> url
        Key.LABEL -> label
        Key.NOTE -> note
    }

    override fun setValue(key: Key, value: TextFieldValue): LinkEntryInput = when (key) {
        Key.URL -> copy(url = value)
        Key.LABEL -> copy(label = value)
        Key.NOTE -> copy(note = value)
    }
}