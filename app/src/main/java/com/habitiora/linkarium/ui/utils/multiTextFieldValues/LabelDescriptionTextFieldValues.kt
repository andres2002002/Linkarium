package com.habitiora.linkarium.ui.utils.multiTextFieldValues

import androidx.compose.ui.text.input.TextFieldValue

data class LabelDescriptionTextFieldValues(
    val label: TextFieldValue = TextFieldValue(""),
    val description: TextFieldValue = TextFieldValue("")
){
    companion object{
        const val LABEL_KEY = "label"
        const val DESCRIPTION_KEY = "description"
    }
}
