package com.habitiora.linkarium.ui.screens.plantSeed

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Tag
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.DataValidator
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LabelDescriptionInput
import com.habitiora.linkarium.ui.utils.multiTextFieldValues.LinkEntryInput

// ─── Design Tokens (coherente con ItemSeed) ───────────────────────────────────
private object PlantSeedTokens {

    object Spacing {
        val XS = 4.dp
        val S = 8.dp
        val M = 16.dp
        val L = 24.dp
        val XL = 32.dp
    }

    val ContentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    val BottomPadding = PaddingValues(bottom = 80.dp)

    /** Grosor del trazo de acento lateral en ExpandablePanel */
    val AccentStrokeWidth = 3.dp

    /** Altura de la cabecera con degradado en SectionCard */
    val SectionHeaderHeight = 52.dp

    /** Duración estándar de animaciones — misma que ItemSeed */
    const val AnimMs = 280
}

// ─── Color helpers ────────────────────────────────────────────────────────────
@Composable
private fun sectionHeaderGradient(alpha: Float = 0.18f): Brush =
    Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = alpha),
            MaterialTheme.colorScheme.secondary.copy(alpha = alpha * 0.4f),
            Color.Transparent
        )
    )

@Composable
private fun getAkariTextFieldColors() = AkariTextFieldDefaults.colors().copy(
    focusedLabelColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    unfocusedLabelColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    disabledLabelColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
)

// ─── Screen ───────────────────────────────────────────────────────────────────
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

    PlantSeedContent(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
private fun PlantSeedContent(
    uiState: PlantSeedUiState,
    onEvent: (PlantSeedEvent) -> Unit
) {
    val focusRequesters = remember { List(5) { FocusRequester() } }
    val colorsTxtFld = getAkariTextFieldColors()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(PlantSeedTokens.ContentPadding),
        contentPadding = PlantSeedTokens.BottomPadding,
        verticalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.M)
    ) {

        // ── Jardín ──────────────────────────────────────────────────────────
        item {
            SectionCard(
                icon = Icons.Default.LocalFlorist,
                title = stringResource(R.string.garden),
                subtitle = stringResource(R.string.select_target_garden)
            ) {
                GardenSelector(
                    currentGarden = uiState.selectedGarden,
                    gardens = uiState.gardens,
                    onClick = { onEvent(PlantSeedEvent.OnGardenChange(it)) }
                )
            }
        }

        // ── Información Básica ───────────────────────────────────────────────
        item {
            SectionCard(
                icon = Icons.Default.Info,
                title = stringResource(R.string.basic_info),
                subtitle = stringResource(R.string.basic_info_description)
            ) {
                NameField(
                    nameTextFieldValue = uiState.nameNotes.label,
                    focusRequester = focusRequesters[0],
                    colorsTxtFld = colorsTxtFld,
                    onNameTextFieldValueChange = {
                        onEvent(
                            PlantSeedEvent.OnNameNotesTextFieldValueChange(
                                LabelDescriptionInput.Key.LABEL, it
                            )
                        )
                    }
                )
                Spacer(Modifier.height(PlantSeedTokens.Spacing.S))
                CoverComponent(
                    coverImageUri = uiState.coverImageUri,
                    coverTextFieldValue = uiState.cover,
                    onCoverTextFieldValueChange = {
                        onEvent(
                            PlantSeedEvent.OnCoverTextFieldValueChange(
                                it
                            )
                        )
                    },
                    colorsTxtFld = colorsTxtFld
                )
            }
        }

        // ── Enlaces ──────────────────────────────────────────────────────────
        item {
            SectionCard(
                icon = Icons.Default.Link,
                title = stringResource(R.string.links),
                subtitle = pluralStringResource(
                    R.plurals.add_link_count_label,
                    uiState.entries.size,
                    uiState.entries.size
                )
            ) {
                LinksComponent(
                    entryTextFieldValues = uiState.newEntry,
                    updateNewEntryTextFieldValues = { key, value ->
                        onEvent(PlantSeedEvent.OnNewEntryTextFieldValueChange(key, value))
                    },
                    focusRequesters = Triple(
                        focusRequesters[1], focusRequesters[2], focusRequesters[3]
                    ),
                    colorsTxtFld = colorsTxtFld,
                    entries = uiState.entries,
                    addLink = { onEvent(PlantSeedEvent.OnAddLink) },
                    editLink = { onEvent(PlantSeedEvent.OnEditLink(it)) },
                    removeLink = { onEvent(PlantSeedEvent.OnRemoveLink(it)) },
                    onMove = { f, t -> onEvent(PlantSeedEvent.OnMoveLink(f, t)) }
                )
            }
        }

        // ── Notas ────────────────────────────────────────────────────────────
        item {
            SectionCard(
                icon = Icons.Default.Notes,
                title = stringResource(R.string.notes),
                subtitle = stringResource(R.string.notes_description)
            ) {
                NotesField(
                    notesTextFieldValue = uiState.nameNotes.description,
                    focusRequester = focusRequesters[4],
                    colorsTxtFld = colorsTxtFld,
                    onNotesTextFieldValueChange = {
                        onEvent(
                            PlantSeedEvent.OnNameNotesTextFieldValueChange(
                                LabelDescriptionInput.Key.DESCRIPTION, it
                            )
                        )
                    }
                )
            }
        }

        // ── Tags ─────────────────────────────────────────────────────────────
        item { TagsComponent() }
    }
}

// ─── SectionCard ─────────────────────────────────────────────────────────────
/**
 * Contenedor premium coherente con ItemSeed:
 * • Cabecera con degradado horizontal (primary → transparent) + icono
 * • Cuerpo sobre fondo surface con divider sutil
 * • shapes.large para bordes redondeados consistentes
 */
@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Cabecera con degradado horizontal
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PlantSeedTokens.SectionHeaderHeight)
                    .background(sectionHeaderGradient())
                    .padding(horizontal = PlantSeedTokens.Spacing.M)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.S)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Cuerpo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PlantSeedTokens.Spacing.M),
                verticalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.S),
                content = content
            )
        }
    }
}

// ─── ExpandablePanel ─────────────────────────────────────────────────────────
/**
 * Panel colapsable premium con trazo de acento lateral (como los chips de ItemSeed).
 * Reemplaza los OutlinedCard con flechas de los componentes originales.
 */
@Composable
private fun ExpandablePanel(
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (expanded)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        animationSpec = tween(PlantSeedTokens.AnimMs),
        label = "PanelBg"
    )
    val accentAlpha by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
        animationSpec = tween(PlantSeedTokens.AnimMs),
        label = "AccentAlpha"
    )
    val arrowTint by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(PlantSeedTokens.AnimMs),
        label = "ArrowTint"
    )

    Column {
        // Header row
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            color = bgColor,
            tonalElevation = 0.dp,
            onClick = onToggle
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Trazo de acento lateral
                Box(
                    modifier = Modifier
                        .width(PlantSeedTokens.AccentStrokeWidth)
                        .height(56.dp)
                        .background(accentAlpha)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = PlantSeedTokens.Spacing.M,
                            vertical = PlantSeedTokens.Spacing.S
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(if (expanded) R.string.hide else R.string.show),
                        tint = arrowTint,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }
            }
        }

        // Contenido expandible
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(PlantSeedTokens.AnimMs)) + expandVertically(),
            exit = fadeOut(tween(PlantSeedTokens.AnimMs)) + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = PlantSeedTokens.Spacing.S,
                        start = PlantSeedTokens.AccentStrokeWidth + PlantSeedTokens.Spacing.M,
                        end = PlantSeedTokens.Spacing.XS,
                        bottom = PlantSeedTokens.Spacing.XS
                    ),
                verticalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.S),
                content = content
            )
        }
    }
}

// ─── GardenSelector ──────────────────────────────────────────────────────────
@Composable
private fun GardenSelector(
    currentGarden: LinkGarden,
    gardens: List<LinkGarden>,
    onClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val accountValid = gardens.any { it.id == currentGarden.id }

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            color = if (expanded)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            tonalElevation = 0.dp,
            onClick = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = PlantSeedTokens.Spacing.M,
                        vertical = PlantSeedTokens.Spacing.S
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.M)
            ) {
                // Icono envuelto en fondo circular sutil
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    if (accountValid) {
                        Text(
                            text = currentGarden.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
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
                    } else {
                        Text(
                            text = stringResource(R.string.select_garden),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.hide else R.string.show),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.large
        ) {
            gardens.forEachIndexed { index, item ->
                val isSelected = item.id == currentGarden.id
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocalFlorist,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
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
                    onClick = { onClick(index); expanded = false },
                    trailingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = stringResource(R.string.selected_item)) }
                    } else null
                )
            }
        }
    }
}

// ─── NameField ────────────────────────────────────────────────────────────────
@Composable
private fun NameField(
    nameTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    colorsTxtFld: TextFieldColors,
    onNameTextFieldValueChange: (TextFieldValue) -> Unit
) {
    val config = rememberAkariTextFieldConfig {
        slots {
            label = { Text(stringResource(R.string.seed_name_label)) }
            placeholder = { Text(stringResource(R.string.seed_name_placeholder)) }
        }
        behavior { singleLine = true }
        style { colors = colorsTxtFld }
    }
    AkariTextField(
        value = nameTextFieldValue,
        onValueChange = onNameTextFieldValueChange,
        config = config,
        focusRequester = focusRequester
    )
}

// ─── NotesField ───────────────────────────────────────────────────────────────
@Composable
private fun NotesField(
    notesTextFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    colorsTxtFld: TextFieldColors,
    onNotesTextFieldValueChange: (TextFieldValue) -> Unit
) {
    val config = rememberAkariTextFieldConfig {
        slots {
            label = { Text(stringResource(R.string.notes_label)) }
            placeholder = { Text(stringResource(R.string.notes_placeholder)) }
        }
        behavior { minLines = 2 }
        style { colors = colorsTxtFld }
    }
    AkariTextField(
        value = notesTextFieldValue,
        onValueChange = onNotesTextFieldValueChange,
        config = config,
        focusRequester = focusRequester
    )
}

// ─── CoverComponent ───────────────────────────────────────────────────────────
@Composable
private fun CoverComponent(
    coverImageUri: Uri?,
    coverTextFieldValue: TextFieldValue,
    onCoverTextFieldValueChange: (TextFieldValue) -> Unit,
    colorsTxtFld: TextFieldColors
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExpandablePanel(
        title = stringResource(R.string.cover_title),
        subtitle = stringResource(R.string.cover_description),
        expanded = expanded,
        onToggle = { expanded = !expanded }
    ) {
        // Preview
        if (coverImageUri != null) {
            AsyncImage(
                model = coverImageUri.toString(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        } else {
            // Placeholder premium
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.XS)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = stringResource(R.string.no_cover),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    )
                }
            }
        }

        val config = rememberAkariTextFieldConfig {
            slots {
                label = { Text(stringResource(R.string.cover_label)) }
                placeholder = { Text(stringResource(R.string.cover_placeholder)) }
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
            }
            behavior { singleLine = true }
            style { colors = colorsTxtFld }
        }
        AkariTextField(
            value = coverTextFieldValue,
            onValueChange = onCoverTextFieldValueChange,
            config = config
        )
    }
}

// ─── LinksComponent ───────────────────────────────────────────────────────────
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

    Column(verticalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.S)) {

        LinksMetaData(
            labelTextFieldValue = entryTextFieldValues.label,
            notesTextFieldValue = entryTextFieldValues.note,
            labelFocusRequester = focusRequesters.second,
            notesFocusRequester = focusRequesters.third,
            colorsTxtFld = colorsTxtFld,
            onLabelTextFieldValueChange = {
                updateNewEntryTextFieldValues(
                    LinkEntryInput.Key.LABEL,
                    it
                )
            },
            onNotesTextFieldValueChange = {
                updateNewEntryTextFieldValues(
                    LinkEntryInput.Key.NOTE,
                    it
                )
            }
        )

        Column {
            LinksTextField(
                modifier = Modifier.fillMaxWidth(),
                newUrlTextFieldValue = entryTextFieldValues.url,
                focusRequester = focusRequesters.first,
                colorsTxtFld = colorsTxtFld,
                onNewUrlTextFieldValueChange = {
                    updateNewEntryTextFieldValues(
                        LinkEntryInput.Key.URL,
                        it
                    )
                },
                enabledAddIcon = isUrlValid,
                onAddLink = { addLink(); showSuccessAnimation = true }
            )

            AnimatedVisibility(
                visible = !urlValidation.isValid && entryTextFieldValues.url.text.isNotBlank()
            ) {
                Text(
                    text = urlValidation.errorMessageRes?.let { stringResource(id = it) }
                        ?: stringResource(R.string.invalid_url),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(
                        start = PlantSeedTokens.Spacing.M,
                        top = PlantSeedTokens.Spacing.XS
                    )
                )
            }
        }

        // Lista de enlaces
        AnimatedVisibility(
            visible = entries.isNotEmpty(),
            enter = fadeIn() + expandVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.S)) {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = PlantSeedTokens.Spacing.XS),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                LinksList(
                    entries = entries,
                    editLink = editLink,
                    removeLink = removeLink,
                    onMove = onMove
                )
            }
        }

        // Empty state
        AnimatedVisibility(visible = entries.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = PlantSeedTokens.Spacing.L),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LinkOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(PlantSeedTokens.Spacing.XS))
                Text(
                    text = stringResource(R.string.no_links_added),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }

    LaunchedEffect(showSuccessAnimation) {
        if (showSuccessAnimation) {
            kotlinx.coroutines.delay(1500)
            showSuccessAnimation = false
        }
    }
}

// ─── LinksMetaData ────────────────────────────────────────────────────────────
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
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExpandablePanel(
        title = stringResource(R.string.link_metadata),
        subtitle = stringResource(R.string.link_metadata_description),
        expanded = expanded,
        onToggle = { expanded = !expanded }
    ) {
        val labelConfig = rememberAkariTextFieldConfig {
            slots {
                label = { Text(stringResource(R.string.link_tag_label)) }
                placeholder = { Text(stringResource(R.string.link_tag_placeholder)) }
            }
            behavior { singleLine = true }
            style { colors = colorsTxtFld }
        }
        val notesConfig = rememberAkariTextFieldConfig {
            slots {
                label = { Text(stringResource(R.string.link_notes_label)) }
                placeholder = { Text(stringResource(R.string.link_notes_placeholder)) }
            }
            behavior { minLines = 2; maxLines = 4 }
            style { colors = colorsTxtFld }
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

// ─── LinksTextField ───────────────────────────────────────────────────────────
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
    val config = rememberAkariTextFieldConfig(enabledAddIcon) {
        slots {
            label = { Text(stringResource(R.string.link_url_label)) }
            placeholder = { Text(stringResource(R.string.link_url_placeholder)) }
            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
            trailingIcon = {
                IconButton(onClick = onAddLink, enabled = enabledAddIcon) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_link))
                }
            }
        }
        behavior { singleLine = true }
        style { colors = colorsTxtFld }
    }
    AkariTextField(
        modifier = modifier,
        value = newUrlTextFieldValue,
        onValueChange = onNewUrlTextFieldValueChange,
        config = config,
        focusRequester = focusRequester
    )
}

// ─── LinksList ────────────────────────────────────────────────────────────────
@Composable
private fun LinksList(
    entries: List<LinkEntry>,
    onMove: (Int, Int) -> Unit,
    editLink: (LinkEntry) -> Unit,
    removeLink: (LinkEntry) -> Unit
) {
    val state = rememberAkariReorderableColumnState<LinkEntry> { from, to -> onMove(from, to) }
    AkariReorderableColumn(
        items = entries,
        state = state,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PlantSeedTokens.Spacing.S),
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

// ─── LinkItem ─────────────────────────────────────────────────────────────────
@Composable
private fun LinkItem(
    modifier: Modifier = Modifier,
    isDragging: Boolean,
    entry: LinkEntry,
    onClear: () -> Unit,
    onEdit: () -> Unit
) {
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 0.dp,
        label = "LinkItemElevation"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isDragging)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        label = "LinkItemBg"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = MaterialTheme.shapes.medium
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = stringResource(R.string.reorder),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
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
                Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onClear) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
    }
}

// ─── TagsComponent ────────────────────────────────────────────────────────────
@Composable
private fun TagsComponent() {
    SectionCard(
        icon = Icons.Default.Tag,
        title = stringResource(R.string.tags_title),
        subtitle = stringResource(R.string.tags_subtitle)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.feature_develop),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}