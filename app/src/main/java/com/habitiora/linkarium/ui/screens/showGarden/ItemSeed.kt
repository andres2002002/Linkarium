package com.habitiora.linkarium.ui.screens.showGarden

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.akari.uicomponents.checkbox.AkariCheckBox
import com.akari.uicomponents.tooltip.AkariTooltip
import com.habitiora.linkarium.R
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.utils.clipBoardHelper.ClipboardHelper
import com.habitiora.linkarium.ui.utils.uirHelper.UriHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun ItemSeed(
    modifier: Modifier = Modifier,
    seed: LinkSeed,
    defaultCoverUri: Uri,
    clipboardHelper: ClipboardHelper,
    urlHelper: UriHelper,
    widthSizeClass: WindowWidthSizeClass,
    scope: CoroutineScope,
    callbacks: ItemSeedCallbacks,
    showSelector: Boolean,
    checked: Boolean,
    shape: Shape = MaterialTheme.shapes.large, // Más redondeado para look premium
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    var showContent by remember { mutableStateOf(false) }

    val isSingleLink = seed.links.size == 1
    val hasLinks = seed.links.isNotEmpty()

    val onMainClick: () -> Unit = {
        if (hasLinks) {
            if (isSingleLink) urlHelper.open(seed.links.first().uri)
            else showContent = !showContent
        }
    }

    val onCopyLink: () -> Unit = {
        if (hasLinks && isSingleLink) {
            scope.launch {
                clipboardHelper.copyAsUri(seed.name, seed.links.first().uri)
            }
        }
    }

    val cardBorderWidth = when {
        !showSelector -> ItemSeedDefaults.BorderWidthSelectModeOff
        checked -> ItemSeedDefaults.BorderWidthSelected
        else -> ItemSeedDefaults.BorderWidthUnselected
    }

    val dynamicElevation by animateDpAsState(
        targetValue = if (checked) ItemSeedDefaults.CardElevationSelected else ItemSeedDefaults.CardElevation,
        animationSpec = tween(ItemSeedDefaults.AnimationDuration),
        label = "CardElevation"
    )
    val dynamicContainerColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primaryContainer else containerColor,
        animationSpec = tween(ItemSeedDefaults.AnimationDuration),
        label = "CardContainerColor"
    )

    Card(
        modifier = modifier.animateContentSize(
            animationSpec = tween(ItemSeedDefaults.AnimationDuration)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dynamicElevation
        ),
        border = CardDefaults.outlinedCardBorder().copy(width = cardBorderWidth),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = dynamicContainerColor,
            contentColor = contentColor
        )
    ) {
        Column {

            // ─── CABECERA PREMIUM: imagen + overlay gradient + info superpuesta ───
            PremiumHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // Más generosa para mayor impacto visual
                    .clip(shape) // Clip que respeta el shape de la Card
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                        onClick = onMainClick,
                        onDoubleClick = { callbacks.onDoubleTap() },
                        onLongClick = { callbacks.onLongPress() }
                    )
                    .semantics { contentDescription = "Seed item: ${seed.name}" },
                seed = seed,
                defaultCoverUri = defaultCoverUri,
                showSelector = showSelector,
                checked = checked,
                onCheckedChange = { callbacks.onCheckedChange(it) },
                isSingleLink = isSingleLink,
                onSingleLink = onCopyLink,
                onMultiLink = { showContent = !showContent },
                showContent = showContent,
                onEdit = { callbacks.onEdit(seed) },
                onDelete = { callbacks.onDelete(seed) },
                widthSizeClass = widthSizeClass
            )

            // ─── NOTAS (solo si existen) ───
            seed.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ─── MULTI-LINKS EXPANDIBLE ───
            MultiLinksContent(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                visible = showContent && !isSingleLink,
                entries = seed.links,
                onClick = { uri -> urlHelper.open(uri) },
                onCopy = { uri ->
                    scope.launch { clipboardHelper.copyAsUri(seed.name, uri) }
                }
            )
        }
    }
}

/**
 * Cabecera premium: imagen de fondo con gradiente scrim oscuro en la parte inferior.
 * El nombre, URL y botones de acción se superponen sobre el degradado,
 * dando un look tipo "magazine card".
 */
@Composable
private fun PremiumHeader(
    modifier: Modifier = Modifier,
    seed: LinkSeed,
    defaultCoverUri: Uri,
    showSelector: Boolean,
    showContent: Boolean,
    widthSizeClass: WindowWidthSizeClass,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isSingleLink: Boolean,
    onSingleLink: () -> Unit,
    onMultiLink: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(modifier = modifier) {

        // 1. Imagen de fondo
        AsyncImage(
            model = seed.coverUri?.toString() ?: defaultCoverUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Gradient scrim: transparente arriba → negro semiopaco abajo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.45f to Color.Black.copy(alpha = 0.15f),
                            1.0f to Color.Black.copy(alpha = 0.72f)
                        )
                    )
                )
        )

        // 3. Selector (esquina superior izquierda) — solo cuando está activo
        if (showSelector) {
            AnimatedVisibility(
                visible = showSelector,
                enter = fadeIn(tween(ItemSeedDefaults.AnimationDuration)),
                exit = fadeOut(tween(ItemSeedDefaults.AnimationDuration)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                AkariCheckBox(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                ) {
                    Icon(
                        modifier = Modifier.size(ItemSeedDefaults.IconSizeLarge),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        // 4. Contenido superpuesto en la parte inferior
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Info: URL + Nombre
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val mainText = when (seed.links.size) {
                    0 -> stringResource(R.string.no_links)
                    1 -> seed.links.first().uri.toString()
                    else -> stringResource(R.string.multiple_links, seed.links.size)
                }

                Text(
                    text = mainText,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = seed.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Botones de acción (sobre el scrim)
            TrailButtons(
                showContent = showContent,
                isSingleLink = isSingleLink,
                onSingleLink = onSingleLink,
                onMultiLink = onMultiLink,
                onEdit = onEdit,
                onDelete = onDelete,
                widthSizeClass = widthSizeClass,
                onDarkBackground = true
            )
        }

        // 5. Chip de tipo (esquina superior derecha) — accesorio visual premium
        val chipIcon = if (isSingleLink) R.drawable.round_link_24 else R.drawable.round_view_list_24

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.40f),
                    shape = MaterialTheme.shapes.extraLarge
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = ImageVector.vectorResource(chipIcon),
                contentDescription = null,
                tint = Color.White
            )
            Text(
                text = pluralStringResource(
                    id = R.plurals.link_count_label,
                    count = seed.links.size,
                    seed.links.size
                ),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrailButtons(
    modifier: Modifier = Modifier,
    isSingleLink: Boolean,
    widthSizeClass: WindowWidthSizeClass,
    showContent: Boolean = false,
    onSingleLink: () -> Unit,
    onMultiLink: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDarkBackground: Boolean = false // Nuevo: ajusta el tinte para fondos oscuros
) {
    val iconRes: Int
    val contentDescId: Int
    val onClick: () -> Unit

    when {
        isSingleLink -> {
            iconRes = R.drawable.round_content_copy_24
            contentDescId = R.string.copy_link
            onClick = onSingleLink
        }
        showContent -> {
            iconRes = R.drawable.round_unfold_less_24
            contentDescId = R.string.hide_more_links
            onClick = onMultiLink
        }
        else -> {
            iconRes = R.drawable.round_unfold_more_24
            contentDescId = R.string.show_more_links
            onClick = onMultiLink
        }
    }

    val targetRotation = if (!isSingleLink && showContent) 180f else 0f
    val iconRotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(ItemSeedDefaults.AnimationDuration),
        label = "Trail Icon Rotation"
    )

    val iconSize = ItemSeedDefaults.IconSizeMedium
    val tintColor = if (onDarkBackground) Color.White else MaterialTheme.colorScheme.primary

    val contentDesc = stringResource(contentDescId)
    Row(
        modifier = modifier.semantics { contentDescription = contentDesc },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AkariTooltip(text = contentDesc) {
            IconButton(onClick = onClick) {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .rotate(iconRotation),
                    imageVector = ImageVector.vectorResource(iconRes),
                    contentDescription = contentDesc,
                    tint = tintColor
                )
            }
        }

        MoreOptions(
            widthSizeClass = widthSizeClass,
            iconSize = iconSize,
            onEdit = onEdit,
            onDelete = onDelete,
            onDarkBackground = onDarkBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreOptions(
    widthSizeClass: WindowWidthSizeClass,
    iconSize: Dp,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDarkBackground: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }
    val iconTint = if (onDarkBackground) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    if (widthSizeClass == WindowWidthSizeClass.Compact) {
        Box(modifier = Modifier.wrapContentSize()) {
            AkariTooltip(text = stringResource(R.string.options)) {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more_options),
                        tint = iconTint
                    )
                }
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
                    },
                    text = { Text(stringResource(R.string.edit)) },
                    onClick = { onEdit(); showMenu = false }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                    },
                    text = { Text(stringResource(R.string.delete)) },
                    onClick = { onDelete(); showMenu = false }
                )
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AkariTooltip(text = stringResource(R.string.edit)) {
                IconButton(onClick = onEdit) {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = iconTint
                    )
                }
            }
            AkariTooltip(text = stringResource(R.string.delete)) {
                IconButton(onClick = onDelete) {
                    Icon(
                        modifier = Modifier.size(iconSize),
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = if (onDarkBackground) Color.White.copy(alpha = 0.85f)
                        else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun MultiLinksContent(
    modifier: Modifier = Modifier,
    visible: Boolean,
    entries: List<LinkEntry>,
    onClick: (Uri) -> Unit,
    onCopy: (Uri) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center
        ) {
            entries.forEachIndexed { index, entry ->
                ItemLink(
                    entry = entry,
                    onClick = { onClick(entry.uri) },
                    onCopy = { onCopy(entry.uri) }
                )
                if (index < entries.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemLink(
    modifier: Modifier = Modifier,
    entry: LinkEntry,
    onClick: () -> Unit,
    onCopy: () -> Unit
) {
    ListItem(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        overlineContent = entry.label?.let { label ->
            {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        headlineContent = {
            SelectionContainer {
                Text(
                    text = entry.uri.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        supportingContent = entry.note?.let { note ->
            {
                Text(
                    text = note,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingContent = {
            Icon(
                modifier = Modifier.size(ItemSeedDefaults.IconSizeMedium),
                imageVector = ImageVector.vectorResource(R.drawable.round_link_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        },
        trailingContent = {
            IconButton(onClick = onCopy) {
                Icon(
                    modifier = Modifier.size(ItemSeedDefaults.IconSizeMedium),
                    imageVector = ImageVector.vectorResource(R.drawable.round_content_copy_24),
                    contentDescription = stringResource(R.string.copy),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}