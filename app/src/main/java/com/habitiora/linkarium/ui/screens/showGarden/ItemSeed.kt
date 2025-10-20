package com.habitiora.linkarium.ui.screens.showGarden

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.habitiora.linkarium.R
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.ui.components.buttons.PlainTooltipIconButton
import com.habitiora.linkarium.ui.components.checkBox.CheckBoxComponent
import com.habitiora.linkarium.ui.utils.clipBoardHelper.ClipboardHelper
import com.habitiora.linkarium.ui.utils.uirHelper.UriHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ItemSeed(
    modifier: Modifier = Modifier,
    seed: LinkSeed,
    clipboardHelper: ClipboardHelper,
    urlHelper: UriHelper,
    widthSizeClass: WindowWidthSizeClass,
    scope: CoroutineScope,
    callbacks: ItemSeedCallbacks,
    showSelector: Boolean,
    checked: Boolean,
    shape: Shape = MaterialTheme.shapes.small,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
){
    var showContent by remember { mutableStateOf(false) }

    // Usar derivedStateOf para evitar recomposiciones innecesarias
    val isSingleLink by remember { derivedStateOf { seed.links.size == 1 } }
    val hasLinks by remember { derivedStateOf { seed.links.isNotEmpty() } }

    // Crear callbacks estables con remember
    val onMainClick = remember(isSingleLink, hasLinks) {
        {
            if (hasLinks) {
                if (isSingleLink) urlHelper.open(seed.links.first().uri)
                else showContent = !showContent
            }
        }
    }

    val onCopyLink = remember(seed.name, isSingleLink, hasLinks) {
        {
            if (hasLinks && isSingleLink) {
                scope.launch {
                    clipboardHelper.copyAsUri(seed.name, seed.links.first().uri)
                }
            }
        }
    }

    val cardBorder = CardDefaults.outlinedCardBorder()
    val cardBorderWidth = remember(showSelector, checked) {
        when {
            !showSelector -> ItemSeedDefaults.BorderWidthSelectModeOff
            checked -> ItemSeedDefaults.BorderWidthSelected
            else -> ItemSeedDefaults.BorderWidthUnselected
        }
    }

    Card(
        modifier = modifier.animateContentSize(
            animationSpec = tween(ItemSeedDefaults.AnimationDuration)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ItemSeedDefaults.CardElevation
        ),
        border = cardBorder.copy(width = cardBorderWidth),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
        ) {
            MainSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = MaterialTheme.colorScheme.primary),
                        onClick = onMainClick,
                        onDoubleClick = callbacks.onDoubleTap,
                        onLongClick = callbacks.onLongPress
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .semantics { contentDescription = "Seed item: ${seed.name}" },
                showSelector = showSelector,
                checked = checked,
                onCheckedChange = callbacks.onCheckedChange,
                isSingleLink = isSingleLink,
                onSingleLink = onCopyLink,
                onMultiLink = { showContent = !showContent },
                seed = seed,
                showContent = showContent,
                onEdit = { callbacks.onEdit(seed) },
                onDelete = { callbacks.onDelete(seed) },
                widthSizeClass = widthSizeClass
            )
            MultiLinksContent(
                modifier = Modifier.padding(start = 16.dp),
                visible = showContent && !isSingleLink,
                entries = seed.links,
                onClick = { uri ->
                    urlHelper.open(uri)
                },
                onCopy = { uri ->
                    scope.launch {
                        clipboardHelper.copyAsUri(seed.name, uri)
                    }
                }
            )
        }
    }
}

@Composable
private fun MainSection(
    modifier: Modifier = Modifier,
    seed: LinkSeed,
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
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeadingSection(
            isSingleLink = isSingleLink,
            showSelector = showSelector,
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        SeedInfo(
            modifier = Modifier.weight(1f),
            seed = seed
        )
        TrailButtons(
            showContent = showContent,
            isSingleLink = isSingleLink,
            onSingleLink = onSingleLink,
            onMultiLink = onMultiLink,
            onEdit = onEdit,
            onDelete = onDelete,
            widthSizeClass = widthSizeClass
        )
    }
}

@Composable
private fun LeadingSection(
    modifier: Modifier = Modifier,
    isSingleLink: Boolean,
    showSelector: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
){
    val iconRes = if (isSingleLink) R.drawable.round_link_24 else R.drawable.round_view_list_24
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        AnimatedVisibility(
            visible = showSelector,
            enter = fadeIn(animationSpec = tween(ItemSeedDefaults.AnimationDuration)),
            exit = fadeOut(animationSpec = tween(ItemSeedDefaults.AnimationDuration))
        ) {
            CheckBoxComponent(
                modifier = Modifier.padding(end = 8.dp),
                checked = checked,
                onCheckedChange = onCheckedChange,
                iconSelected = {
                    Icon(
                        modifier = Modifier.size(ItemSeedDefaults.IconSizeLarge),
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
            )
        }

        AnimatedVisibility(
            visible = !showSelector,
            enter = fadeIn(animationSpec = tween(ItemSeedDefaults.AnimationDuration)),
            exit = fadeOut(animationSpec = tween(ItemSeedDefaults.AnimationDuration))
        ) {
            Icon(
                modifier = Modifier.size(ItemSeedDefaults.IconSizeLarge),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SeedInfo(
    modifier: Modifier = Modifier,
    seed: LinkSeed
){
    val mainText = remember(seed.links.size) {
        when(seed.links.size){
            0 -> "No links"
            1 -> seed.links.first().uri.toString()
            else -> "${seed.links.size} links"
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = mainText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = seed.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        seed.notes?.takeIf { it.isNotBlank() }?.let { notes ->
            Text(
                text = notes,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TrailButtons(
    modifier: Modifier = Modifier,
    isSingleLink: Boolean,
    widthSizeClass: WindowWidthSizeClass,
    showContent: Boolean = false,
    onSingleLink: () -> Unit,
    onMultiLink: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val (iconRes, contentDescription, onClick) = remember(isSingleLink, showContent) {
        when {
            isSingleLink ->
                Triple(R.drawable.round_content_copy_24, "Copy link", onSingleLink)

            showContent ->
                Triple(R.drawable.round_unfold_less_24, "Hide more links", onMultiLink)

            else ->
                Triple(R.drawable.round_unfold_more_24, "Show more links", onMultiLink)
        }
    }

    val iconRotation by animateFloatAsState(
        targetValue = if (showContent) 180f else 0f,
        animationSpec = tween(ItemSeedDefaults.AnimationDuration),
        label = "Trail Icon Rotation"
    )

    val buttonSize = ItemSeedDefaults.IconSizeMedium + 8.dp
    val iconSize = ItemSeedDefaults.IconSizeMedium

    Row(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlainTooltipIconButton(
            modifier = Modifier.size(buttonSize),
            tooltipText = contentDescription,
            onClick = onClick
        ) {
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .rotate(iconRotation),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = contentDescription
            )
        }
        MoreOptions(
            widthSizeClass = widthSizeClass,
            buttonSize = buttonSize,
            iconSize = iconSize,
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreOptions(
    widthSizeClass: WindowWidthSizeClass,
    buttonSize: Dp,
    iconSize: Dp,
    onEdit: () -> Unit,
    onDelete: () -> Unit
){
    var showMenu by remember { mutableStateOf(false) }

    if (widthSizeClass == WindowWidthSizeClass.Compact) {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            PlainTooltipIconButton(
                modifier = Modifier.size(buttonSize),
                tooltipText = "Options",
                onClick = { showMenu = !showMenu }
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    },
                    text = { Text("Edit") },
                    onClick = { onEdit(); showMenu = false }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                    },
                    text = { Text("Delete") },
                    onClick = { onDelete(); showMenu = false }
                )
            }
        }
    } else {
        PlainTooltipIconButton(
            modifier = Modifier.size(buttonSize),
            tooltipText = "Edit",
            onClick = onEdit
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit"
            )
        }
        PlainTooltipIconButton(
            modifier = Modifier.size(buttonSize),
            tooltipText = "Delete",
            onClick = onDelete
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = "Delete"
            )
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
){
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center
        ) {
            entries.forEach { entry ->
                ItemLink(
                    entry = entry,
                    onClick = { onClick(entry.uri) },
                    onCopy = { onCopy(entry.uri) }
                )
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
){
    ListItem(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        overlineContent = entry.label?.let{ label ->
            {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        headlineContent = {
            SelectionContainer {
                Text(
                    text = entry.uri.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        supportingContent = entry.note?.let{ note ->
            {
                Text(
                    text = note,
                    style = MaterialTheme.typography.labelMedium,
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
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            IconButton(onClick = onCopy) {
                Icon(
                    modifier = Modifier.size(ItemSeedDefaults.IconSizeMedium),
                    imageVector = ImageVector.vectorResource(R.drawable.round_content_copy_24),
                    contentDescription = "Copy",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}