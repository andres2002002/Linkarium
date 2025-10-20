package com.habitiora.linkarium.ui.utils.multiTextFieldValues

import androidx.compose.ui.text.input.TextFieldValue

data class LinkEntryTextFieldValues(
    val label: TextFieldValue = TextFieldValue(""),
    val url: TextFieldValue = TextFieldValue(""),
    val note: TextFieldValue = TextFieldValue("")
){
    companion object {
        const val NOTE_KEY = "note"
        const val URL_KEY = "url"
        const val LABEL_KEY = "label"
    }
}