package com.habitiora.linkarium.ui.screens.gardensScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.akari.uicomponents.reorderableComponents.AkariReorderableLazyColumn
import com.akari.uicomponents.reorderableComponents.DragActivation
import com.akari.uicomponents.reorderableComponents.rememberAkariReorderableLazyState
import com.habitiora.linkarium.R
import com.habitiora.linkarium.domain.model.LinkGarden

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun GardensScreen(
    viewModel: GardensViewModel = hiltViewModel()
) {
    val gardens by viewModel.gardens.collectAsState()

    GardensContent(
        modifier  = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        gardens     = gardens,
        onClick     = viewModel::navigateToShowSeeds,
        onMove      = viewModel::moveGarden,
        onDragStart = viewModel::onDragStart,
        onDragEnd   = viewModel::onDragEnd,
        onEdit      = viewModel::onEditGarden,
        onAddGarden = viewModel::onAddGarden
    )
}

// ─── GardensContent ───────────────────────────────────────────────────────────
@Composable
private fun GardensContent(
    modifier: Modifier = Modifier,
    gardens: List<LinkGarden>,
    onClick: (LinkGarden) -> Unit,
    onMove: (Int, Int) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onEdit: (LinkGarden) -> Unit,
    onAddGarden: () -> Unit
) {
    val state = rememberAkariReorderableLazyState<LinkGarden>(
        onMove      = { from, to -> onMove(from, to) },
        onDragEnd   = onDragEnd,
        onDragStart = onDragStart
    )

    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AddNewGarden(
            modifier = Modifier.fillMaxWidth(),
            onClick  = onAddGarden
        )

        AkariReorderableLazyColumn(
            modifier            = Modifier.weight(1f),
            items               = gardens,
            state               = state,
            key                 = { item -> item.id },
            contentPadding      = PaddingValues(bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            dragActivation      = DragActivation.LongPress
        ) { item, isDragging ->
            GardenItem(
                modifier   = Modifier.akariDragHandle(),
                isDragging = isDragging,
                garden     = item,
                onClick    = { onClick(item) },
                onEdit     = { onEdit(item) }
            )
        }
    }
}

// ─── GardenItem ───────────────────────────────────────────────────────────────
/**
 * Card premium con:
 * - shapes.large (coherente con el sistema)
 * - Degradado horizontal sutil al frente izquierdo cuando no se arrastra
 * - Elevación animada al arrastrar (igual que LinkItem en PlantSeedScreen)
 * - Icono en contenedor con fondo primary.copy(alpha=0.12f) (igual que GardenSelector)
 */
@Composable
private fun GardenItem(
    modifier: Modifier = Modifier,
    isDragging: Boolean = false,
    garden: LinkGarden,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    val elevation by animateDpAsState(
        targetValue   = if (isDragging) 8.dp else 1.dp,
        animationSpec = tween(280),
        label         = "GardenItemElevation"
    )
    val containerColor by animateColorAsState(
        targetValue   = if (isDragging)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.surface,
        animationSpec = tween(280),
        label         = "GardenItemColor"
    )

    Card(
        modifier  = modifier.fillMaxWidth(),
        onClick   = onClick,
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                // Degradado horizontal sutil igual que SectionCard / DialogHeader
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = if (isDragging) 0f else 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono en contenedor cuadrado — mismo patrón que GardenSelector y DialogHeader
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector      = Icons.Filled.LocalFlorist,
                    contentDescription = null,
                    tint             = MaterialTheme.colorScheme.primary,
                    modifier         = Modifier.size(22.dp)
                )
            }

            // Nombre + descripción
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text       = garden.name,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                garden.description.takeIf { it.isNotBlank() }?.let { description ->
                    Text(
                        text      = description,
                        style     = MaterialTheme.typography.bodySmall,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic,
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis
                    )
                }
            }

            // Botón de edición
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector      = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.more_options),
                    tint             = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ─── AddNewGarden ─────────────────────────────────────────────────────────────
/**
 * Tarjeta de acción primaria:
 * - Degradado horizontal más pronunciado (alpha 0.18f) para destacar como CTA
 * - Icono envuelto en contenedor con fondo primary, no surfaceVariant
 * - shapes.large coherente con el sistema
 */
@Composable
private fun AddNewGarden(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier,
        onClick   = onClick,
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.07f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono con fondo primary sólido — diferenciador visual del CTA
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector      = Icons.Default.Add,
                    contentDescription = null,
                    tint             = MaterialTheme.colorScheme.onPrimary,
                    modifier         = Modifier.size(22.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text       = stringResource(R.string.new_garden),
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = stringResource(R.string.add_new_garden_description),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}