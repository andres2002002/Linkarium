package com.habitiora.linkarium.ui.screens.gardenManager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
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
        onSave = { viewModel.saveGarden() }
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
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(250)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(250)
            ),
            exit = fadeOut(tween(200)) + scaleOut(
                targetScale = 0.9f,
                animationSpec = tween(200)
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
                onDismiss = onDismissRequest,
                onSave = onSave
            )
        }
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
    onDismiss: () -> Unit,
    onSave: () -> Unit
){
    Card (
        modifier = Modifier
            .padding(24.dp)
            .sizeIn(
                minWidth = 320.dp,
                maxWidth = 560.dp
            ),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header con título y botón cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New Garden",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido scrolleable
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción
            ActionButtons(
                enabled = enabledButton,
                onSave = onSave,
                onCancel = onDismiss
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
            label = { Text("Garden Name") }
            placeholder = "ex. My Favorite Links"
            this.focusRequester = focusRequester
            behavior {
                singleLine = true
                maxLines = 1
                minLines = 1
            }
        }
    )

    AkariTextField(state = nameState)

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
            label = { Text("Description (Optional)") }
            placeholder = "Add a description for this garden..."
            this.focusRequester = focusRequester
            behavior {
                singleLine = false
                maxLines = 5
                minLines = 2
            }
        }
    )

    AkariTextField(state = descriptionState)

}

@Composable
private fun ActionButtons(
    enabled: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onCancel,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        FilledTonalButton(
            onClick = onSave,
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Create",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}