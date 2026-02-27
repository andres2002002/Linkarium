package com.habitiora.linkarium.ui.utils.multiTextFieldValues

import androidx.compose.ui.text.input.TextFieldValue

/**
 * Contrato base para los estados de entrada de formularios.
 * @param K El tipo de Enum que representa las claves de los campos.
 * @param T El tipo de la implementación concreta (Genérico recursivo para el retorno de setValue).
 */
interface FormInputState<K : Enum<K>, T : FormInputState<K, T>> {
    fun getValue(key: K): TextFieldValue
    fun setValue(key: K, value: TextFieldValue): T
}