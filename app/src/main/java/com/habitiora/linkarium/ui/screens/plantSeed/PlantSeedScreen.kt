package com.habitiora.linkarium.ui.screens.plantSeed

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.akari.uicomponents.reorderableComponents.AkariReorderableColumn
import com.akari.uicomponents.reorderableComponents.rememberAkariReorderableColumnState
import com.akari.uicomponents.textFields.AkariTextField
import com.akari.uicomponents.textFields.internalConfig.AkariTextFieldDefaults
import com.akari.uicomponents.textFields.rememberAkariTextFieldConfig
import com.habitiora.linkarium.core.DataValidator
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionInput
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryInput

private val PaddingSmall = 4.dp

// Constantes de diseño centralizadas
private object DesignTokens {
    object Padding {
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Medium = 16.dp
        val Large = 24.dp
        val ExtraLarge = 32.dp
    }

    val ContentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    val BottomPadding = PaddingValues(bottom = 80.dp)

    val CardElevation = 2.dp
    val CornerRadius = 16.dp

    val containerColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
}

@Composable
private fun getAkariTextFieldColors() = AkariTextFieldDefaults.colors().copy(
    focusedLabelColor = DesignTokens.containerColor,
    unfocusedLabelColor = DesignTokens.containerColor,
    disabledLabelColor = DesignTokens.containerColor
)

@Composable
fun PlantSeedScreen(
    viewModel: PlantSeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.addSeedStatus) {
        if (uiState.addSeedStatus.isSuccess()) {
            viewModel.onEvent(PlantSeedEvent.ConsumeStatusAndBackStack)
        }
    }

    PlantSeedContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun PlantSeedContent(
    uiState: PlantSeedUiState,
    onEvent: (PlantSeedEvent) -> Unit
) {
    val focusRequesters = remember {
        List(5) { FocusRequester() }
    }

    val colorsTxtFld = getAkariTextFieldColors()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(DesignTokens.ContentPadding),
        contentPadding = DesignTokens.BottomPadding,
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Medium)
    ) {
        item {
            SectionCard(
                title = "Jardín",
                subtitle = "Selecciona el jardín al que deseas agregar esta semilla"
            ) {
                GardenSelector(
                    currentGarden = uiState.selectedGarden,
                    gardens = uiState.gardens,
                    onClick = { onEvent(PlantSeedEvent.OnGardenChange(it)) }
                )
            }
        }

        // Nombre de la Semilla
        item {
            SectionCard(
                title = "Información Básica",
                subtitle = "Dale un nombre identificable a tu semilla"
            ) {
                NameField(
                    nameTextFieldValue = uiState.nameNotes.label,
                    focusRequester = focusRequesters[0],
                    colorsTxtFld = colorsTxtFld,
                    onNameTextFieldValueChange = {
                        onEvent(
                            PlantSeedEvent.OnNameNotesTextFieldValueChange(
                                LabelDescriptionInput.Key.LABEL,
                                it
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(PaddingSmall))
                CoverComponent(
                    coverImageUri = uiState.coverImageUri,
                    coverTextFieldValue = uiState.cover,
                    onCoverTextFieldValueChange = {
                        onEvent(PlantSeedEvent.OnCoverTextFieldValueChange(it))
                    },
                    colorsTxtFld = colorsTxtFld
                )
            }
        }

        // Enlaces
        item {
            SectionCard(
                title = "Enlaces",
                subtitle = "${uiState.entries.size} enlace(s) agregado(s)"
            ) {
                LinksComponent(
                    entryTextFieldValues = uiState.newEntry,
                    updateNewEntryTextFieldValues = { key, value ->
                        onEvent(PlantSeedEvent.OnNewEntryTextFieldValueChange(key, value))
                    },
                    focusRequesters = Triple(
                        focusRequesters[1],
                        focusRequesters[2],
                        focusRequesters[3]
                    ),
                    colorsTxtFld = colorsTxtFld,
                    entries = uiState.entries,
                    addLink = { onEvent(PlantSeedEvent.OnAddLink) },
                    editLink = { onEvent(PlantSeedEvent.OnEditLink(it)) },
                    removeLink = { onEvent(PlantSeedEvent.OnRemoveLink(it)) },
                    onMove = { from, to -> onEvent(PlantSeedEvent.OnMoveLink(from, to)) }
                )
            }
        }

        // Notas
        item {
            SectionCard(
                title = "Notas",
                subtitle = "Información adicional sobre esta semilla"
            ) {
                NotesField(
                    notesTextFieldValue = uiState.nameNotes.description,
                    focusRequester = focusRequesters[4],
                    colorsTxtFld = colorsTxtFld,
                    onNotesTextFieldValueChange = {
                        onEvent(
                            PlantSeedEvent.OnNameNotesTextFieldValueChange(
                                LabelDescriptionInput.Key.DESCRIPTION,
                                it
                            )
                        )
                    }
                )
            }
        }
        item { TagsComponent() }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Small)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = DesignTokens.containerColor
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignTokens.Padding.Small),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignTokens.Padding.ExtraSmall),
            content = content
        )
    }
}

@Composable
private fun NameField(
    nameTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    colorsTxtFld: TextFieldColors,
    onNameTextFieldValueChange: (TextFieldValue) -> Unit
) {
    val config = rememberAkariTextFieldConfig {
        slots {
            label = { Text("Nombre de la semilla") }
            placeholder = { Text("Ej: Recursos de aprendizaje") }
        }
        behavior {
            singleLine = true
        }
        style {
            colors = colorsTxtFld
        }
    }
    AkariTextField(
        value = nameTextFieldValue,
        onValueChange = onNameTextFieldValueChange,
        config = config,
        focusRequester = focusRequester
    )
}

@Composable
private fun NotesField(
    notesTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    colorsTxtFld: TextFieldColors,
    onNotesTextFieldValueChange: (TextFieldValue) -> Unit
) {
    val config = rememberAkariTextFieldConfig {
        slots {
            label = { Text("Notas generales") }
            placeholder = { Text("Añade contexto o recordatorios...") }
        }
        behavior {
            minLines = 2
        }
        style {
            colors = colorsTxtFld
        }
    }

    AkariTextField(
        value = notesTextFieldValue,
        onValueChange = onNotesTextFieldValueChange,
        config = config,
        focusRequester = focusRequester
    )
}

@Composable
private fun CoverComponent(
    coverImageUri: Uri?,
    coverTextFieldValue: TextFieldValue,
    onCoverTextFieldValueChange: (TextFieldValue) -> Unit,
    colorsTxtFld: TextFieldColors
) {

    var visible by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Small)) {
        OutlinedCard(
            onClick = { visible = !visible },
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (visible) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignTokens.Padding.Medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cover de la semilla",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Coloca una imagen para complementar el contenido.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (visible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (visible) "Ocultar" else "Mostrar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Small)) {

                val config = rememberAkariTextFieldConfig {
                    slots {
                        label = { Text("URL") }
                        placeholder = { Text("https://ejemplo.com") }
                        leadingIcon = {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Default.Link,
                                contentDescription = null
                            )
                        }
                    }
                    behavior {
                        singleLine = true
                    }
                    style {
                        colors = colorsTxtFld
                    }
                }

                if (coverImageUri != null){
                    AsyncImage(
                        model = coverImageUri.toString(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                else{
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image", style = MaterialTheme.typography.labelSmall)
                    }
                }

                AkariTextField(
                    value = coverTextFieldValue,
                    onValueChange = onCoverTextFieldValueChange,
                    config = config,
                )
            }
        }
    }
}

@Composable
private fun LinksComponent(
    entryTextFieldValues: LinkEntryInput,
    updateNewEntryTextFieldValues: (LinkEntryInput.Key, TextFieldValue) -> Unit,
    focusRequesters: Triple<FocusRequester, FocusRequester, FocusRequester>,
    colorsTxtFld: TextFieldColors,
    entries: List<LinkEntry>,
    addLink: () -> Unit,
    editLink: (LinkEntry) -> Unit,
    removeLink: (LinkEntry) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    var showSuccessAnimation by remember { mutableStateOf(false) }
    val urlValidation = remember(entryTextFieldValues.url.text) {
        DataValidator.validateUrl(entryTextFieldValues.url.text)
    }
    val isUrlValid = urlValidation.isValid && entryTextFieldValues.url.text.isNotBlank()

    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Small)
    ) {
        // Metadata toggle
        LinksMetaData(
            labelTextFieldValue = entryTextFieldValues.label,
            notesTextFieldValue = entryTextFieldValues.note,
            labelFocusRequester = focusRequesters.second,
            notesFocusRequester = focusRequesters.third,
            colorsTxtFld = colorsTxtFld,
            onLabelTextFieldValueChange = {
                updateNewEntryTextFieldValues(LinkEntryInput.Key.LABEL, it)
            },
            onNotesTextFieldValueChange = {
                updateNewEntryTextFieldValues(LinkEntryInput.Key.NOTE, it)
            }
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinksTextField(
                    modifier = Modifier.fillMaxWidth(),
                    newUrlTextFieldValue = entryTextFieldValues.url,
                    focusRequester = focusRequesters.first,
                    colorsTxtFld = colorsTxtFld,
                    onNewUrlTextFieldValueChange = {
                        updateNewEntryTextFieldValues(LinkEntryInput.Key.URL, it)
                    },
                    enabledAddIcon = isUrlValid,
                    onAddLink = {
                        addLink()
                        showSuccessAnimation = true
                    }
                )
            }

            // Mensaje de error
            AnimatedVisibility(
                visible = !urlValidation.isValid && entryTextFieldValues.url.text.isNotBlank()
            ) {
                Text(
                    text = urlValidation.errorMessageRes?.let { stringResource(id = it) }
                        ?: "URL inválida",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(
                        start = DesignTokens.Padding.Medium,
                        top = DesignTokens.Padding.ExtraSmall
                    )
                )
            }
        }

        // Lista de enlaces
        AnimatedVisibility(
            visible = entries.isNotEmpty(),
            enter = fadeIn() + expandVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Small)) {
                HorizontalDivider(modifier = Modifier.padding(bottom = DesignTokens.Padding.Small))
                LinksList(
                    entries = entries,
                    editLink = editLink,
                    removeLink = removeLink,
                    onMove = onMove
                )
            }
        }

        // Mensaje cuando no hay enlaces
        AnimatedVisibility(visible = entries.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DesignTokens.Padding.Large),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay enlaces agregados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Animación de éxito
    LaunchedEffect(showSuccessAnimation) {
        if (showSuccessAnimation) {
            kotlinx.coroutines.delay(1500)
            showSuccessAnimation = false
        }
    }
}

@Composable
private fun LinksMetaData(
    labelTextFieldValue: TextFieldValue,
    notesTextFieldValue: TextFieldValue,
    labelFocusRequester: FocusRequester,
    notesFocusRequester: FocusRequester,
    colorsTxtFld: TextFieldColors,
    onLabelTextFieldValueChange: (TextFieldValue) -> Unit,
    onNotesTextFieldValueChange: (TextFieldValue) -> Unit
) {

    var isAddMetadata by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Small)) {
        OutlinedCard(
            onClick = { isAddMetadata = !isAddMetadata },
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (isAddMetadata) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignTokens.Padding.Medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Metadatos del enlace",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Etiqueta y notas opcionales",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (isAddMetadata) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isAddMetadata) "Ocultar" else "Mostrar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(
            visible = isAddMetadata,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Small)) {

                val labelConfig = rememberAkariTextFieldConfig {
                    slots {
                        label = { Text("Etiqueta") }
                        placeholder = { Text("Ej: Documentación oficial") }
                    }
                    behavior {
                        singleLine = true
                    }
                    style {
                        colors = colorsTxtFld
                    }
                }

                val notesConfig = rememberAkariTextFieldConfig {
                    slots {
                        label = { Text("Notas del enlace") }
                        placeholder = { Text("Información adicional...") }
                    }
                    behavior {
                        minLines = 2
                        maxLines = 4
                    }
                    style {
                        colors = colorsTxtFld
                    }
                }

                AkariTextField(
                    value = labelTextFieldValue,
                    onValueChange = onLabelTextFieldValueChange,
                    config = labelConfig,
                    focusRequester = labelFocusRequester
                )
                AkariTextField(
                    value = notesTextFieldValue,
                    onValueChange = onNotesTextFieldValueChange,
                    config = notesConfig,
                    focusRequester = notesFocusRequester
                )
            }
        }
    }
}

@Composable
private fun LinksTextField(
    modifier: Modifier = Modifier,
    newUrlTextFieldValue: TextFieldValue = TextFieldValue(""),
    focusRequester: FocusRequester,
    colorsTxtFld: TextFieldColors,
    onNewUrlTextFieldValueChange: (TextFieldValue) -> Unit = {},
    enabledAddIcon: Boolean = true,
    onAddLink: () -> Unit = {}
) {
    val config = rememberAkariTextFieldConfig(
        enabledAddIcon
    ) {
        slots {
            label = { Text("URL") }
            placeholder = { Text("https://ejemplo.com") }
            leadingIcon = {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.Link,
                    contentDescription = null
                )
            }
            trailingIcon = {
                IconButton(
                    onClick = onAddLink,
                    enabled = enabledAddIcon
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar enlace"
                    )
                }
            }
        }
        behavior {
            singleLine = true
        }
        style {
            colors = colorsTxtFld
        }
    }

    AkariTextField(
        modifier = modifier,
        value = newUrlTextFieldValue,
        onValueChange = onNewUrlTextFieldValueChange,
        config = config,
        focusRequester = focusRequester
    )
}

@Composable
private fun LinksList(
    entries: List<LinkEntry>,
    onMove: (Int, Int) -> Unit,
    editLink: (LinkEntry) -> Unit,
    removeLink: (LinkEntry) -> Unit
) {
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
) {
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 0.dp,
        label = "elevation"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = "Reordenar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            overlineContent = entry.label?.let { label ->
                {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            headlineContent = {
                Text(
                    text = entry.uri.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = entry.note?.let { note ->
                {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            trailingContent = {
                Row(horizontalArrangement = Arrangement.spacedBy(DesignTokens.Padding.ExtraSmall)) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar enlace")
                    }
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar enlace")
                    }
                }
            }
        )
    }
}

@Composable
private fun TagsComponent() {
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
    currentGarden: LinkGarden,
    gardens: List<LinkGarden>,
    onClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val accountValid = gardens.any { it.id == currentGarden.id }

    Box(modifier = Modifier) {
        OutlinedCard(
            onClick = { expanded = !expanded },
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (expanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                else
                    Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = DesignTokens.Padding.Medium,
                        vertical = DesignTokens.Padding.Small
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DesignTokens.Padding.Medium),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    if (accountValid) {
                        Column {
                            Text(
                                text = currentGarden.name,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            currentGarden.description.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Seleccionar jardín",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier,
            shape = MaterialTheme.shapes.medium
        ) {
            gardens.forEachIndexed { index, item ->
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = if (item.id == currentGarden.id) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            item.description.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    onClick = {
                        onClick(index)
                        expanded = false
                    },
                    trailingIcon = if (item.id == currentGarden.id) {
                        { Icon(Icons.Default.Check, contentDescription = "Seleccionado") }
                    } else null
                )
            }
        }
    }
}