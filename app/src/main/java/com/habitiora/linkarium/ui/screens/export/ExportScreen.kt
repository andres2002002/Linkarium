package com.habitiora.linkarium.ui.screens.export

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.akari.uicomponents.checkbox.AkariCheckBox
import com.habitiora.linkarium.core.exporters.ExportFormat
import com.habitiora.linkarium.core.exporters.ExportStatus
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.ui.utils.ExportSelectionMode

object ExportTokens{
    object Padding{
        val extraSmall = 4.dp
        val small = 8.dp
        val medium = 16.dp
        val large = 32.dp
        val extraLarge = 64.dp
    }

}
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

private fun ExportFormat?.createFileName(name: String = "Linkarium_Backup") = "$name.${this?.extension}"

@Composable
fun ExportProgressDialog(
    status: ExportStatus,
    onDismiss: () -> Unit
) {
    if (status is ExportStatus.Idle) return

    AlertDialog(
        onDismissRequest = { /* Bloquear cierre si está cargando, o permitir cancelar */ },
        title = { Text(text = "Exportando Datos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (status) {
                    is ExportStatus.InProgress -> {
                        Text("Procesando item ${status.current} de ${status.total}")
                        LinearProgressIndicator(
                            progress = { status.percentage },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is ExportStatus.Success -> {
                        Text("¡Exportación completada exitosamente!")
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
                    }
                    is ExportStatus.Error -> {
                        Text("Error: ${status.exception.localizedMessage}")
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            if (status !is ExportStatus.InProgress) {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        }
    )
}

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
){
    var isExpanded by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.padding(ExportTokens.Padding.medium)
    ) {
        item {
            MainSection(
                selectedFormat = format,
                onFormatSelected = onFormatSelected,
                onExport = onExport
            )
        }
        item {
            AnimatedVisibility(
                visible = format != ExportFormat.Backup
            ) {
                OptionsDivider(
                    isExpanded = isExpanded,
                    onClick = { isExpanded = !isExpanded }
                )
            }
        }
        item {
            AnimatedVisibility(
                visible = isExpanded && format != ExportFormat.Backup
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

// region MainSection
@Composable
private fun MainSection(
    selectedFormat: ExportFormat? = null,
    onFormatSelected: (ExportFormat) -> Unit = {},
    onExport: () -> Unit = {}
){
    val formats = ExportFormat.allFormats
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ExportTokens.Padding.medium)
        ) {
            Text(text = "Selecciona un formato para exportar")
            FormatSelection(
                formats = formats,
                selectedFormat = selectedFormat,
                onFormatSelected = onFormatSelected
            )
            ExportButton(
                format = selectedFormat,
                onClick = onExport
            )
        }
    }
}

@Composable
private fun FormatSelection(
    formats: List<ExportFormat>,
    selectedFormat: ExportFormat?,
    onFormatSelected: (ExportFormat) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    OutlinedButton(
        modifier = Modifier
            .padding(ExportTokens.Padding.medium),
        onClick = { expanded = true }
    ){
        Text(text = selectedFormat?.name ?: "Selecciona un formato")
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            formats.forEach { format ->
                DropdownMenuItem(
                    text = { Text(text = format.name) },
                    onClick = {
                        onFormatSelected(format)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ExportButton(
    format: ExportFormat?,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        enabled = format != null
    ) {
        Text(text = "Exportar ${format?.name ?: "--"}")
    }
}

//endregion

@Composable
private fun OptionsDivider(
    isExpanded: Boolean,
    onClick: () -> Unit
){
    val text = "Avanzado"
    val icon = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = ExportTokens.Padding.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ExportTokens.Padding.small)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
        Icon(icon, contentDescription = null)
    }
}

@Composable
private fun SelectContentExport(
    gardensSelected: List<Long>,
    gardens: List<LinkGarden>,
    onSelectGarden: (Long) -> Unit,
    selection: ExportSelectionMode,
    onSelectionChange: (ExportSelectionMode) -> Unit
){
    Column(
        modifier = Modifier
            .padding(horizontal = ExportTokens.Padding.medium)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButtonItem(
                modifier = Modifier.weight(1f),

                selected = selection == ExportSelectionMode.AllGardens,
                onClick = { onSelectionChange(ExportSelectionMode.AllGardens) },
                text = "Todas mis collections"
            )
            RadioButtonItem(
                modifier = Modifier.weight(1f),
                selected = selection == ExportSelectionMode.SelectedGardens,
                onClick = { onSelectionChange(ExportSelectionMode.SelectedGardens) },
                text = "Solo seleccionadas: ${gardensSelected.size} / ${gardens.size}"
            )
        }
        AnimatedVisibility(
            visible = selection == ExportSelectionMode.SelectedGardens,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                HorizontalDivider(modifier = Modifier.padding(vertical = ExportTokens.Padding.small))
                Text(
                    text = "Selecciona las collections a exportar: ${gardensSelected.size} / ${gardens.size} collections",
                    style = MaterialTheme.typography.labelMedium,
                    fontStyle = FontStyle.Italic
                )
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(3),
                    contentPadding = PaddingValues(ExportTokens.Padding.small),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(ExportTokens.Padding.small),
                    horizontalArrangement = Arrangement.spacedBy(ExportTokens.Padding.medium)
                ) {
                    items(gardens, key = { item -> item.id }) { item ->
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

@Composable
private fun RadioButtonItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    text: String
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = ExportTokens.Padding.small)
        )
    }
}

@Composable
private fun GardenSelectionItem(
    modifier: Modifier = Modifier,
    garden: LinkGarden,
    selected: Boolean,
    onClick: () -> Unit
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AkariCheckBox(
            checked = selected,
            onCheckedChange = { onClick() }
        ){
            Icon(Icons.Filled.Check, contentDescription = null)
        }
        Text(
            text = garden.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = ExportTokens.Padding.small)
        )
    }
}