package com.habitiora.linkarium.ui.screens.export

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.akari.uicomponents.checkbox.AkariCheckBox
import com.habitiora.linkarium.R
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.ExportSelectionMode

// ─── Helpers ──────────────────────────────────────────────────────────────────
private fun ExportFormat?.createFileName(name: String = "Linkarium_Backup") =
    "$name.${this?.extension}"

@Composable
private fun sectionGradient() = Brush.horizontalGradient(
    listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.07f),
        Color.Transparent
    )
)

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun ExportScreen(
    viewModel: ExportViewModel = hiltViewModel()
) {
    val exportStatus by viewModel.exportStatus.collectAsState()
    val exportFormat by viewModel.exportFormat.collectAsState()
    val exportSelectionMode by viewModel.exportSelectionMode.collectAsState()
    val gardens by viewModel.gardens.collectAsState()
    val gardensSelected by viewModel.gardensSelected.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.export(uri)
    }

    ExportProgressDialog(
        status = exportStatus,
        onDismiss = { viewModel.resetStatus() }
    )

    // Usamos un array de MimeTypes. Si usaste una extensión custom (.gdn),
    // el OS suele asignarle "application/octet-stream" o "*/*".
    ExportContentScreen(
        format = exportFormat,
        exportSelectionMode = exportSelectionMode,
        onExport = { launcher.launch(exportFormat.createFileName()) },
        onFormatSelected = viewModel::setExportFormat,
        gardens = gardens,
        gardensSelected = gardensSelected,
        onSelectionChange = viewModel::selectionChange,
        onSelectGarden = viewModel::selectGarden
    )
}

// ─── ExportContentScreen ─────────────────────────────────────────────────────
@Composable
private fun ExportContentScreen(
    format: ExportFormat?,
    exportSelectionMode: ExportSelectionMode,
    onExport: () -> Unit,
    onFormatSelected: (ExportFormat) -> Unit,
    gardens: List<LinkGarden> = emptyList(),
    gardensSelected: List<Long> = emptyList(),
    onSelectionChange: (ExportSelectionMode) -> Unit,
    onSelectGarden: (Long) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.padding(ExportTokens.Spacing.M),
        verticalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.M)
    ) {
        item {
            MainSection(
                selectedFormat = format,
                onFormatSelected = onFormatSelected,
                onExport = onExport,
            )
        }

        item {
            AnimatedVisibility(visible = format != ExportFormat.Backup) {
                AdvancedDivider(
                    isExpanded = isExpanded,
                    onClick = { isExpanded = !isExpanded }
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = isExpanded && format != ExportFormat.Backup,
                enter = fadeIn(tween(ExportTokens.AnimMs)) + expandVertically(),
                exit = fadeOut(tween(ExportTokens.AnimMs)) + shrinkVertically()
            ) {
                SelectContentExport(
                    gardensSelected = gardensSelected,
                    gardens = gardens,
                    onSelectGarden = onSelectGarden,
                    selection = exportSelectionMode,
                    onSelectionChange = onSelectionChange
                )
            }
        }
    }
}

// ─── MainSection ──────────────────────────────────────────────────────────────
/**
 * Card premium con cabecera de degradado — igual que SectionCard de PlantSeedScreen.
 */
@Composable
private fun MainSection(
    selectedFormat: ExportFormat? = null,
    onFormatSelected: (ExportFormat) -> Unit = {},
    onExport: () -> Unit = {},
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
                    .background(sectionGradient())
                    .padding(horizontal = ExportTokens.Spacing.M, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.export_data),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.select_export_format),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    .padding(ExportTokens.Spacing.M),
                verticalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.M)
            ) {
                FormatSelector(
                    formats = ExportFormat.allFormats,
                    selectedFormat = selectedFormat,
                    onFormatSelected = onFormatSelected
                )

                // Botón de exportar — coherente con ActionButtons del diálogo
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onExport,
                    enabled = selectedFormat != null,
                    shape = MaterialTheme.shapes.large,
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(ExportTokens.Spacing.S))
                    val formatName = selectedFormat?.name ?: stringResource(id = R.string.placeholder_dash)

                    Text(
                        text = stringResource(id = R.string.export_format_label, formatName),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─── FormatSelector ───────────────────────────────────────────────────────────
/**
 * Selector de formato con Surface interactivo — mismo estilo que GardenSelector
 * en PlantSeedScreen: contenedor surfaceVariant + icono cuadrado + chevron.
 */
@Composable
private fun FormatSelector(
    formats: List<ExportFormat>,
    selectedFormat: ExportFormat?,
    onFormatSelected: (ExportFormat) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            color = if (expanded)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            tonalElevation = 0.dp,
            onClick = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = ExportTokens.Spacing.M,
                        vertical = ExportTokens.Spacing.S
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.M)
            ) {
                // Icono en contenedor — mismo patrón del sistema
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedFormat?.name ?: stringResource(R.string.select_export_format_option),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedFormat != null) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedFormat != null) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    selectedFormat?.let {
                        Text(
                            text = ".${it.extension}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(if (expanded) R.string.contract else R.string.expand),
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
            formats.forEach { format ->
                val isSelected = format == selectedFormat
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    text = {
                        Text(
                            text = format.name,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    trailingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null,
                    onClick = { onFormatSelected(format); expanded = false }
                )
            }
        }
    }
}

// ─── AdvancedDivider ──────────────────────────────────────────────────────────
/**
 * Divisor expandible "Avanzado" — mismo patrón de ExpandablePanel pero
 * como separador inline, con flecha animada coherente con el sistema.
 */
@Composable
private fun AdvancedDivider(
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(vertical = ExportTokens.Spacing.S, horizontal = ExportTokens.Spacing.XS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)
    ) {
        Icon(
            imageVector = Icons.Outlined.Tune,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = stringResource(R.string.advanced),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(18.dp)
                .rotate(if (isExpanded) 180f else 0f)
        )
    }
}

// ─── SelectContentExport ──────────────────────────────────────────────────────
/**
 * Panel de selección avanzada — envuelto en SettingsCard-style con
 * los RadioButtons y la grilla de jardines.
 */
@Composable
private fun SelectContentExport(
    gardensSelected: List<Long>,
    gardens: List<LinkGarden>,
    onSelectGarden: (Long) -> Unit,
    selection: ExportSelectionMode,
    onSelectionChange: (ExportSelectionMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Mini-cabecera coherente con SectionCard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(sectionGradient())
                    .padding(horizontal = ExportTokens.Spacing.M, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.content_selection),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ExportTokens.Spacing.M),
                verticalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)
            ) {
                // Radio buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)
                ) {
                    SelectionChip(
                        modifier = Modifier.weight(1f),
                        selected = selection == ExportSelectionMode.AllGardens,
                        onClick = { onSelectionChange(ExportSelectionMode.AllGardens) },
                        text = stringResource(R.string.all_gardens),
                        icon = Icons.Outlined.SelectAll
                    )
                    SelectionChip(
                        modifier = Modifier.weight(1f),
                        selected = selection == ExportSelectionMode.SelectedGardens,
                        onClick = { onSelectionChange(ExportSelectionMode.SelectedGardens) },
                        text = stringResource(R.string.selected_gardens,
                            gardensSelected.size,
                            gardens.size
                        ),
                        icon = Icons.Outlined.FilterList
                    )
                }

                // Grilla de jardines
                AnimatedVisibility(
                    visible = selection == ExportSelectionMode.SelectedGardens,
                    enter = fadeIn(tween(ExportTokens.AnimMs)) + expandVertically(),
                    exit = fadeOut(tween(ExportTokens.AnimMs)) + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)) {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = stringResource(R.string.select_export_gardens),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyHorizontalGrid(
                            rows = GridCells.Fixed(3),
                            contentPadding = PaddingValues(ExportTokens.Spacing.XS),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 150.dp),
                            verticalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.XS),
                            horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.S)
                        ) {
                            items(gardens, key = { it.id }) { item ->
                                GardenSelectionItem(
                                    garden = item,
                                    selected = gardensSelected.contains(item.id),
                                    onClick = { onSelectGarden(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── SelectionChip ────────────────────────────────────────────────────────────
/**
 * Chip de selección de modo — mismo patrón de Surface interactiva
 * que GardenSelector y FormatSelector.
 */
@Composable
private fun SelectionChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        tonalElevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ExportTokens.Spacing.S,
                vertical = ExportTokens.Spacing.S
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ExportTokens.Spacing.XS)
        ) {
            Icon(
                imageVector = if (selected) Icons.Default.CheckCircle else icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── GardenSelectionItem ──────────────────────────────────────────────────────
@Composable
private fun GardenSelectionItem(
    modifier: Modifier = Modifier,
    garden: LinkGarden,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        else
            Color.Transparent,
        tonalElevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ExportTokens.Spacing.S,
                vertical = ExportTokens.Spacing.XS
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AkariCheckBox(
                checked = selected,
                onCheckedChange = { onClick() }
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
            Text(
                text = garden.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = ExportTokens.Spacing.XS)
            )
        }
    }
}