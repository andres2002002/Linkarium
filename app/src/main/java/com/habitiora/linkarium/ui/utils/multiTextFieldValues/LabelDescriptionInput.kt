package com.habitiora.linkarium.ui.utils.multiTextFieldValues

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Representa el estado inmutable de los campos de texto para la captura de etiquetas y descripciones.
 * * Se utiliza [TextFieldValue] en lugar de [String] para preservar el estado completo
 * del componente de texto en Jetpack Compose, incluyendo la posición del cursor y la selección actual,
 * evitando así el parpadeo o pérdida de foco durante las recomposiciones.
 *
 * @property label Estado actual del campo de texto destinado al nombre de la etiqueta.
 * @property description Estado actual del campo de texto destinado a la descripción detallada.
 */
@Immutable
data class LabelDescriptionInput(
    val label: TextFieldValue = TextFieldValue(""),
    val description: TextFieldValue = TextFieldValue("")
) : FormInputState<LabelDescriptionInput.Key, LabelDescriptionInput> {

    enum class Key {
        LABEL,
        DESCRIPTION
    }

    override fun getValue(key: Key): TextFieldValue = when (key) {
        Key.LABEL -> label
        Key.DESCRIPTION -> description
    }

    override fun setValue(key: Key, value: TextFieldValue): LabelDescriptionInput = when (key) {
        Key.LABEL -> copy(label = value)
        Key.DESCRIPTION -> copy(description = value)
    }
}