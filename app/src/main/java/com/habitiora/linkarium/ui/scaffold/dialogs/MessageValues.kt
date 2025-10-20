package com.habitiora.linkarium.ui.scaffold.dialogs

data class MessageValues(
    val type: DialogType,
    val title: String? = null,
    val message: String? = null,
    val details: String? = null,
    val buttons: Map<String, () -> Unit> = emptyMap()
)