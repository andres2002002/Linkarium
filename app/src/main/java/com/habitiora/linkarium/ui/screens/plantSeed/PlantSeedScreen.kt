package com.habitiora.linkarium.ui.screens.plantSeed

import android.net.Uri
import android.widget.Space
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.DataValidator
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.components.textField.ProTextFieldState
import com.habitiora.linkarium.ui.components.textField.RoundedTextFieldPro

@Composable
fun PlantSeedScreen(
    viewModel: PlantSeedViewModel = hiltViewModel(),
    popBackStack: () -> Unit
){
    val nameTextFieldValue by viewModel.nameTextFieldValue.collectAsState()
    val newUrlTextFieldValue by viewModel.newUrlTextFieldValue.collectAsState()
    val notesTextFieldValue by viewModel.notesTextFieldValue.collectAsState()
    val linksList: List<Uri> by viewModel.urlList.collectAsState()
    val linksListString = linksList.map { it.toString() }
    val collectionId by viewModel.collectionId.collectAsState()
    val gardens by viewModel.gardens.collectAsState()
    val isValidSeed by viewModel.isValidSeed.collectAsState()

    PlantSeedContent(
        gardenId = collectionId,
        gardens = gardens,
        onGardenChange = { garden ->
            viewModel.setCollectionId(garden.id)
        },
        nameTextFieldValue = nameTextFieldValue,
        onNameTextFieldValueChange = viewModel::setNameTextFieldValue,
        newUrlTextFieldValue = newUrlTextFieldValue,
        onNewUrlTextFieldValueChange = viewModel::setNewUrlTextFieldValue,
        linksList = linksListString,
        addLink = viewModel::addUrl,
        removeLink = viewModel::removeUrl,
        notesTextFieldValue = notesTextFieldValue,
        onNotesTextFieldValueChange = viewModel::setNotesTextFieldValue,
        isValidSeed = isValidSeed,
        onSave = { viewModel.saveSeed(onSuccess = popBackStack) }
    )
}

@Composable
private fun PlantSeedContent(
    gardenId: Long,
    gardens: List<LinkGarden>,
    onGardenChange: (LinkGarden) -> Unit,
    nameTextFieldValue: TextFieldValue,
    onNameTextFieldValueChange: (TextFieldValue) -> Unit,
    newUrlTextFieldValue: TextFieldValue,
    onNewUrlTextFieldValueChange: (TextFieldValue) -> Unit = {},
    linksList: List<String>,
    addLink: (String) -> Unit,
    removeLink: (String) -> Unit,
    notesTextFieldValue: TextFieldValue,
    onNotesTextFieldValueChange: (TextFieldValue) -> Unit = {},
    isValidSeed: Boolean,
    onSave: () -> Unit
){
    val (nameFocusRequester, newUrlFocusRequester, notesFocusRequester) = remember { FocusRequester.createRefs() }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            GardenDropDown(
                currentGardenId = gardenId,
                gardens = gardens,
                onClick = onGardenChange
            )
        }
        item { NameFieldAndFavorites(
            nameTextFieldValue = nameTextFieldValue,
            focusRequester = nameFocusRequester,
            onNameTextFieldValueChange = onNameTextFieldValueChange
        ) }
        item { LinksComponent(
            newUrlTextFieldValue = newUrlTextFieldValue,
            focusRequester = newUrlFocusRequester,
            onNewUrlTextFieldValueChange = onNewUrlTextFieldValueChange,
            linksList = linksList,
            addLink = addLink,
            removeLink = removeLink
        ) }
        item { NotesComponent(
            notesTextFieldValue = notesTextFieldValue,
            focusRequester = notesFocusRequester,
            onNotesTextFieldValueChange = onNotesTextFieldValueChange
        ) }
        item { TagsComponent() }
        item { SaveButton(
            onSave = onSave,
            enabled = isValidSeed
        ) }
        item { Spacer(modifier = Modifier.height(64.dp)) }
    }
}

@Composable
private fun GardenDropDown(
    currentGardenId: Long,
    gardens: List<LinkGarden>,
    onClick: (LinkGarden) -> Unit
){
    val currentGarden = remember(currentGardenId) {
        gardens.find { it.id == currentGardenId } ?: DatabaseContract.LinkGarden.Empty
    }
    GardenSelector(
        currentGarden = currentGarden,
        gardens = gardens,
        onClick = onClick
    )
}

@Composable
private fun NameFieldAndFavorites(
    nameTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onNameTextFieldValueChange: (TextFieldValue) -> Unit
){
    val nameState = remember(nameTextFieldValue) {
        ProTextFieldState(
            value = nameTextFieldValue,
            onValueChange = onNameTextFieldValueChange,
            focusRequester = focusRequester
        )
    }
    RoundedTextFieldPro(state = nameState)
}

@Composable
private fun LinksComponent(
    newUrlTextFieldValue: TextFieldValue = TextFieldValue(""),
    focusRequester: FocusRequester,
    onNewUrlTextFieldValueChange: (TextFieldValue) -> Unit = {},
    linksList: List<String>,
    addLink: (String) -> Unit,
    removeLink: (String) -> Unit
){
    val isUrlValid = remember(newUrlTextFieldValue.text) {
        DataValidator.validateUrl(newUrlTextFieldValue.text).isValid
    }
    LinksTextField(
        newUrlTextFieldValue = newUrlTextFieldValue,
        focusRequester = focusRequester,
        onNewUrlTextFieldValueChange = onNewUrlTextFieldValueChange
    )
    LinksButton(
        addLink = { addLink(newUrlTextFieldValue.text) },
        enabled = isUrlValid
    )
    LinksList(
        linksList = linksList,
        removeLink = removeLink
    )
}

@Composable
private fun LinksTextField(
    newUrlTextFieldValue: TextFieldValue = TextFieldValue(""),
    focusRequester: FocusRequester,
    onNewUrlTextFieldValueChange: (TextFieldValue) -> Unit = {}
){
    val newUrlState = remember(newUrlTextFieldValue, focusRequester) {
        ProTextFieldState(
            value = newUrlTextFieldValue,
            onValueChange = onNewUrlTextFieldValueChange,
            focusRequester = focusRequester
        )
    }
    RoundedTextFieldPro(state = newUrlState)
}

@Composable
private fun LinksButton(
    addLink: () -> Unit,
    enabled: Boolean,
){
    IconButton(
        onClick = { addLink() },
        enabled = enabled
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Link")
    }
}

@Composable
private fun LinksList(
    linksList: List<String>,
    removeLink: (String) -> Unit
){
    Column {
        linksList.forEach { link ->
            Text(link)
            IconButton(
                onClick = { removeLink(link) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Remove Link")
            }
        }
    }
}

@Composable
private fun NotesComponent(
    notesTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onNotesTextFieldValueChange: (TextFieldValue) -> Unit = {}
){
    val notesState = remember(notesTextFieldValue, focusRequester) {
        ProTextFieldState(
            value = notesTextFieldValue,
            onValueChange = onNotesTextFieldValueChange,
            focusRequester = focusRequester
        )
    }
    RoundedTextFieldPro(state = notesState)
}

@Composable
private fun TagsComponent(){
    TextField(
        value = "",
        onValueChange = {},
        label = { Text(text = "Etiquetas") }
    )
}

@Composable
private fun GardenSelector(
    modifier: Modifier = Modifier,
    currentGarden: LinkGarden,
    gardens: List<LinkGarden>,
    onClick: (LinkGarden) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val accountValid = gardens.any { it.id == currentGarden.id }

    Box(modifier = Modifier){
        Card(
            modifier = modifier
                .animateContentSize(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            ),
            onClick = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                if (accountValid) {
                    Column(modifier = Modifier) {
                        Text(
                            text = currentGarden.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentGarden.description,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.select_garden),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        DropdownMenu(
            modifier = Modifier
                .widthIn(max = 300.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.medium
        ) {
            gardens.filter { it.id != currentGarden.id }.forEach { item ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.name)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    onClick = {
                        onClick(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SaveButton(
    onSave: () -> Unit,
    enabled: Boolean
){
    Button(
        onClick = onSave,
        enabled = enabled
    ) {
        Text("Save")
    }
}