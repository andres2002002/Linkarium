package com.habitiora.linkarium.ui.screens.plantSeed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.akari.uicomponents.reorderableComponents.AkariReorderableColumn
import com.akari.uicomponents.reorderableComponents.rememberAkariReorderableColumnState
import com.akari.uicomponents.textFields.AkariTextField
import com.akari.uicomponents.textFields.rememberAkariOutlinedTextFieldState
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.DataValidator
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.localNavigator.LocalNavigator
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionTextFieldValues
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryTextFieldValues

private val PaddingSmall = 4.dp

@Composable
fun PlantSeedScreen(
    viewModel: PlantSeedViewModel = hiltViewModel()
){
    val nameNotesTextFieldValue by viewModel.nameNotesTextFieldValue.collectAsState()
    val newEntryTextFieldValues by viewModel.newEntryTextFieldValues.collectAsState()
    val entries: List<LinkEntry> by viewModel.entries.collectAsState()
    val collectionId by viewModel.gardenId.collectAsState()
    val gardens by viewModel.gardens.collectAsState()
    val isValidSeed by viewModel.isValidSeed.collectAsState()

    val navController = LocalNavigator.current

    PlantSeedContent(
        gardenId = collectionId,
        gardens = gardens,
        onGardenChange = { garden ->
            viewModel.setGardenId(garden.id)
        },
        nameNotesTextFieldValue = nameNotesTextFieldValue,
        updateNameNotesTextFieldValue = viewModel::updateNameNotesTextFieldValue,
        newEntryTextFieldValues = newEntryTextFieldValues,
        updateNewEntryTextFieldValues = viewModel::updateNewEntryTextFieldValues,
        entries = entries,
        addLink = viewModel::addEntryOfCurrent,
        editLink = viewModel::editEntry,
        removeLink = viewModel::removeEntry,
        onMove = viewModel::moveEntry,
        isValidSeed = isValidSeed,
        onSave = { viewModel.saveSeed(onSuccess = {navController.popBackStack()}) }
    )
}

@Composable
private fun PlantSeedContent(
    gardenId: Long,
    gardens: List<LinkGarden>,
    onGardenChange: (LinkGarden) -> Unit,
    nameNotesTextFieldValue: LabelDescriptionTextFieldValues,
    updateNameNotesTextFieldValue: (String, TextFieldValue) -> Unit,
    newEntryTextFieldValues: LinkEntryTextFieldValues,
    updateNewEntryTextFieldValues: (String, TextFieldValue) -> Unit,
    entries: List<LinkEntry>,
    addLink: () -> Unit,
    editLink: (LinkEntry) -> Unit,
    removeLink: (LinkEntry) -> Unit,
    onMove: (Int, Int) -> Unit,
    isValidSeed: Boolean,
    onSave: () -> Unit
){
    val (entryUrlFocusRequester, entryLabelFocusRequester, entryNotesFocusRequester) = remember { FocusRequester.createRefs() }
    val (nameFocusRequester, notesFocusRequester) = remember { FocusRequester.createRefs() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GardenDropDown(
                currentGardenId = gardenId,
                gardens = gardens,
                onClick = onGardenChange
            )
        }
        item {
            NameFieldAndFavorites(
                nameTextFieldValue = nameNotesTextFieldValue.label,
                focusRequester = nameFocusRequester,
                onNameTextFieldValueChange = {
                    updateNameNotesTextFieldValue(
                        LabelDescriptionTextFieldValues.LABEL_KEY,
                        it
                    )
                }
            )
        }
        item {
            LinksComponent(
                entryTextFieldValues = newEntryTextFieldValues,
                updateNewEntryTextFieldValues = updateNewEntryTextFieldValues,
                focusRequesters = Triple(entryUrlFocusRequester, entryLabelFocusRequester, entryNotesFocusRequester),
                entries = entries,
                addLink = addLink,
                editLink = editLink,
                removeLink = removeLink,
                onMove = onMove
            )
        }
        item {
            NotesComponent(
                notesTextFieldValue = nameNotesTextFieldValue.description,
                focusRequester = notesFocusRequester,
                onNotesTextFieldValueChange = {
                    updateNameNotesTextFieldValue(
                        LabelDescriptionTextFieldValues.DESCRIPTION_KEY,
                        it
                    )
                }
            )
        }
        item { TagsComponent() }
        item {
            SaveButton(
                onSave = onSave,
                enabled = isValidSeed
            )
        }
    }
}

@Composable
private fun GardenDropDown(
    currentGardenId: Long,
    gardens: List<LinkGarden>,
    onClick: (LinkGarden) -> Unit
){
    val currentGarden = remember(currentGardenId, gardens.size) {
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
    val nameState = rememberAkariOutlinedTextFieldState(
        value = nameTextFieldValue,
        onValueChange = onNameTextFieldValueChange,
        builder = {
            label = { Text("Name") }
            this.focusRequester = focusRequester
        }
    )
    AkariTextField(state = nameState)
}

@Composable
private fun LinksComponent(
    entryTextFieldValues: LinkEntryTextFieldValues,
    updateNewEntryTextFieldValues: (String, TextFieldValue) -> Unit,
    focusRequesters: Triple<FocusRequester, FocusRequester, FocusRequester>,
    entries: List<LinkEntry>,
    addLink: () -> Unit,
    editLink: (LinkEntry) -> Unit,
    removeLink: (LinkEntry) -> Unit,
    onMove: (Int, Int) -> Unit
){
    val isUrlValid = remember(entryTextFieldValues.url.text) {
        DataValidator.validateUrl(entryTextFieldValues.url.text).isValid && entryTextFieldValues.url.text.isNotBlank()
    }
    val (urlFocusRequester, labelFocusRequester, notesFocusRequester) = focusRequesters
    Text("Add link")
    Spacer(modifier = Modifier.height(PaddingSmall))
    LinksMetaData(
        labelTextFieldValue = entryTextFieldValues.label,
        notesTextFieldValue = entryTextFieldValues.note,
        labelFocusRequester = labelFocusRequester,
        notesFocusRequester = notesFocusRequester,
        onLabelTextFieldValueChange = {
            updateNewEntryTextFieldValues(LinkEntryTextFieldValues.LABEL_KEY, it)
        },
        onNotesTextFieldValueChange = {
            updateNewEntryTextFieldValues(LinkEntryTextFieldValues.NOTE_KEY, it)
        }
    )
    Spacer(modifier = Modifier.height(2 * PaddingSmall))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinksTextField(
            modifier = Modifier.weight(1f),
            newUrlTextFieldValue = entryTextFieldValues.url,
            focusRequester = urlFocusRequester,
            onNewUrlTextFieldValueChange = {
                updateNewEntryTextFieldValues(LinkEntryTextFieldValues.URL_KEY, it)
            }
        )
        LinksButton(
            modifier = Modifier,
            addLink = addLink,
            enabled = isUrlValid
        )
    }
    Spacer(modifier = Modifier.height(PaddingSmall))
    LinksList(
        entries = entries,
        editLink = editLink,
        removeLink = removeLink,
        onMove = onMove
    )
}

@Composable
private fun LinksMetaData(
    modifier: Modifier = Modifier,
    labelTextFieldValue: TextFieldValue,
    notesTextFieldValue: TextFieldValue,
    labelFocusRequester: FocusRequester,
    notesFocusRequester: FocusRequester,
    onLabelTextFieldValueChange: (TextFieldValue) -> Unit,
    onNotesTextFieldValueChange: (TextFieldValue) -> Unit
){

    val labelState = rememberAkariOutlinedTextFieldState(
        value = labelTextFieldValue,
        onValueChange = onLabelTextFieldValueChange,
        builder = {
            label = { Text("Label") }
            this.focusRequester = labelFocusRequester
        }
    )

    val notesState = rememberAkariOutlinedTextFieldState(
        value = notesTextFieldValue,
        onValueChange = onNotesTextFieldValueChange,
        builder = {
            label = { Text("Notes") }
            this.focusRequester = notesFocusRequester
        }
    )

    var isAddMetadata by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isAddMetadata,
                onCheckedChange = { isAddMetadata = it }
            )
            Text(
                text = "Add Metadata",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = "Only visible for multiple links",
            style = MaterialTheme.typography.labelMedium
        )
        AnimatedVisibility(
            visible = isAddMetadata,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row {
                    AkariTextField(
                        state = labelState,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(PaddingSmall))
                Row {
                    AkariTextField(
                        state = notesState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LinksTextField(
    modifier: Modifier = Modifier,
    newUrlTextFieldValue: TextFieldValue = TextFieldValue(""),
    focusRequester: FocusRequester,
    onNewUrlTextFieldValueChange: (TextFieldValue) -> Unit = {}
) {
    val newUrlState = rememberAkariOutlinedTextFieldState(
        value = newUrlTextFieldValue,
        onValueChange = onNewUrlTextFieldValueChange,
        builder = {
            label = { Text("URL") }
            this.focusRequester = focusRequester
        }
    )

    AkariTextField(
        state = newUrlState,
        modifier = modifier
    )
}

@Composable
private fun LinksButton(
    modifier: Modifier = Modifier,
    addLink: () -> Unit,
    enabled: Boolean,
){
    IconButton(
        modifier = modifier,
        onClick = { addLink() },
        enabled = enabled
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Link")
    }
}

@Composable
private fun LinksList(
    entries: List<LinkEntry>,
    onMove: (Int, Int) -> Unit,
    editLink: (LinkEntry) -> Unit,
    removeLink: (LinkEntry) -> Unit
){
    val state = rememberAkariReorderableColumnState<LinkEntry> { from, to ->
        onMove(from, to)
    }
    AkariReorderableColumn(
        items = entries,
        state = state,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) { entry, isDragging ->
        LinkItem(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .akariDragHandle(),
            isDragging = isDragging,
            entry = entry,
            onEdit = { editLink(entry) },
            onClear = { removeLink(entry) }
        )
    }
}

@Composable
private fun LinkItem(
    modifier: Modifier = Modifier,
    isDragging: Boolean,
    entry: LinkEntry,
    onClear: () -> Unit,
    onEdit: () -> Unit,
){
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = if (isDragging) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        ),
        overlineContent = entry.label?.let { label ->
            {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        headlineContent = {
            Text(
                text = entry.uri.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingContent = entry.note?.let { note ->
            {
                Text(
                    text = note,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Link")
                }
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear Link")
                }
            }
        }
    )
}

@Composable
private fun NotesComponent(
    modifier: Modifier = Modifier,
    notesTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    onNotesTextFieldValueChange: (TextFieldValue) -> Unit = {}
){
    val notesState = rememberAkariOutlinedTextFieldState(
        value = notesTextFieldValue,
        onValueChange = onNotesTextFieldValueChange,
        builder = {
            label = { Text("Notes") }
            this.focusRequester = focusRequester
        }
    )

    AkariTextField(modifier = modifier, state = notesState)
}

@Composable
private fun TagsComponent(){
    Text("Tags")
    Spacer(modifier = Modifier.height(PaddingSmall))
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