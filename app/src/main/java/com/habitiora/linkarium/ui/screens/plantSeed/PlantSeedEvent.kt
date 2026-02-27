package com.habitiora.linkarium.ui.screens.plantSeed

import androidx.compose.ui.text.input.TextFieldValue
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionInput
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryInput

sealed interface PlantSeedEvent {
    data class OnGardenChange(val index: Int) : PlantSeedEvent
    data class OnNameNotesTextFieldValueChange(val key: LabelDescriptionInput.Key, val value: TextFieldValue) : PlantSeedEvent
    data class OnNewEntryTextFieldValueChange(val key: LinkEntryInput.Key, val value: TextFieldValue) : PlantSeedEvent
    data class OnCoverTextFieldValueChange(val value: TextFieldValue) : PlantSeedEvent
    object OnAddLink : PlantSeedEvent
    data class OnEditLink(val link: LinkEntry) : PlantSeedEvent
    data class OnRemoveLink(val link: LinkEntry) : PlantSeedEvent
    data class OnMoveLink(val from: Int, val to: Int) : PlantSeedEvent
    data object ConsumeStatusAndBackStack : PlantSeedEvent
}