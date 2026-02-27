package com.habitiora.linkarium.ui.screens.plantSeed

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionInput
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryInput

@Immutable
data class PlantSeedUiState(
    val isEditMode: Boolean = false,
    val nameNotes: LabelDescriptionInput = LabelDescriptionInput(),
    val cover: TextFieldValue = TextFieldValue(),
    val newEntry: LinkEntryInput = LinkEntryInput(),
    val entries: List<LinkEntry> = emptyList(),
    val coverImageUri: Uri? = null,
    val isValidSeed: Boolean = false,
    val addSeedStatus: ProcessStatus<Boolean> = ProcessStatus.Empty,
    val gardens: List<LinkGarden> = emptyList(),
    val selectedGarden: LinkGarden = DatabaseContract.LinkGarden.Empty
)