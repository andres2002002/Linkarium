package com.habitiora.linkarium.ui.screens.gardenManager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.akari.uicomponents.textFields.AkariTextField
import com.akari.uicomponents.textFields.rememberAkariOutlinedTextFieldState

@Composable
fun GardenManagerDialog(
    viewModel: GardenManagerViewModel = hiltViewModel(),
    onDismiss: () -> Unit
){
    val nameTextFieldValue by viewModel.nameTextFieldValue.collectAsState()
    val descriptionTextFieldValue by viewModel.descriptionTextFieldValue.collectAsState()
    val enabledButton by viewModel.isValidGarden.collectAsState()

    ContentScreen(
        nameTextFieldValue = nameTextFieldValue,
        onNameChange = viewModel::setNameTextFieldValue,
        descriptionTextFieldValue = descriptionTextFieldValue,
        onDescriptionChange = viewModel::setDescriptionTextFieldValue,
        onDismissRequest = onDismiss,
        enabledButton = enabledButton,
        onSave = { viewModel.saveGarden(onSuccess = onDismiss) }
    )
}

@Composable
private fun ContentScreen(
    nameTextFieldValue: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit = {},
    descriptionTextFieldValue: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    enabledButton: Boolean,
    onSave: () -> Unit
){
    val (nameFocusRequester, descriptionFocusRequester) = remember { FocusRequester.createRefs()}
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        ContentDialog(
            nameTextFieldValue = nameTextFieldValue,
            nameFocusRequester = nameFocusRequester,
            onNameChange = onNameChange,
            descriptionTextFieldValue = descriptionTextFieldValue,
            descriptionFocusRequester = descriptionFocusRequester,
            onDescriptionChange = onDescriptionChange,
            enabledButton = enabledButton,
            onSave = onSave
        )
    }
}

@Composable
private fun ContentDialog(
    nameTextFieldValue: TextFieldValue,
    nameFocusRequester: FocusRequester,
    onNameChange: (TextFieldValue) -> Unit = {},
    descriptionTextFieldValue: TextFieldValue,
    descriptionFocusRequester: FocusRequester,
    onDescriptionChange: (TextFieldValue) -> Unit = {},
    enabledButton: Boolean,
    onSave: () -> Unit
){
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "New Garden",
                style = MaterialTheme.typography.titleMedium
            )
            NameSection(
                nameTextFieldValue = nameTextFieldValue,
                focusRequester = nameFocusRequester,
                onNameChange = onNameChange
            )
            DescriptionSection(
                descriptionTextFieldValue = descriptionTextFieldValue,
                focusRequester = descriptionFocusRequester,
                onDescriptionChange = onDescriptionChange
            )
            SaveButton(
                enabled = enabledButton,
                onSave = onSave
            )
        }
    }
}

@Composable
private fun NameSection(
    nameTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onNameChange: (TextFieldValue) -> Unit = {}
){
    val nameState = rememberAkariOutlinedTextFieldState(
        value = nameTextFieldValue,
        onValueChange = onNameChange,
        builder = {
            label = { Text("Name") }
            placeholder = "Insert a name"
            this.focusRequester = focusRequester
        }
    )

    Column {
        AkariTextField(state = nameState)
    }
}

@Composable
private fun DescriptionSection(
    descriptionTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onDescriptionChange: (TextFieldValue) -> Unit = {}
){
    val descriptionState = rememberAkariOutlinedTextFieldState(
        value = descriptionTextFieldValue,
        onValueChange = onDescriptionChange,
        builder = {
            label = { Text("Description") }
            this.focusRequester = focusRequester
            placeholder = "Insert a description"
        }
    )

    Column {
        AkariTextField(state = descriptionState)
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean,
    onSave: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = onSave,
            enabled = enabled
        ) {
            Text("Save")
        }
    }

}