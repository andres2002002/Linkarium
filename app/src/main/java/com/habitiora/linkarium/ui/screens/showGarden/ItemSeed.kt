package com.habitiora.linkarium.ui.screens.showGarden

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.habitiora.linkarium.R
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
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
    var showMenu by remember { mutableStateOf(false) }

    // Memorizar valores calculados
    val isSingleLink by remember(seed.links.size) {
        mutableStateOf(seed.links.size == 1)
    }

    val interactionSource = remember { MutableInteractionSource() }

    val copyUri: (String, Uri) -> Unit = remember {
        { name, uri ->
            scope.launch {
                clipboardHelper.copyAsUri(name, uri)
            }
        }
    }

    Card(
        modifier = modifier.animateContentSize(
            animationSpec = tween(ItemSeedDefaults.AnimationDuration)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = ItemSeedDefaults.CardElevation
        ),
        border = when {
            !showSelector -> CardDefaults.outlinedCardBorder().copy(width = ItemSeedDefaults.BorderWidthSelectModeOff)
            checked -> CardDefaults.outlinedCardBorder().copy(width = ItemSeedDefaults.BorderWidthSelected)
            else -> CardDefaults.outlinedCardBorder().copy(width = ItemSeedDefaults.BorderWidthUnselected)
        },
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
                        interactionSource = interactionSource,
                        indication = ripple(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            if (seed.links.isNotEmpty()) {
                                if (isSingleLink) urlHelper.open(seed.links.first().uri)
                                else showContent = !showContent
                            }
                        },
                        onDoubleClick = callbacks.onDoubleTap,
                        onLongClick = callbacks.onLongPress
                    )
                    .padding(top = 8.dp, bottom = 2.dp)
                    .padding(horizontal = 16.dp)
                    .semantics { contentDescription = "Seed item: ${seed.name}" },
                showSelector = showSelector,
                checked = checked,
                onCheckedChange = callbacks.onCheckedChange,
                isSingleLink = isSingleLink,
                onSingleLink = { copyUri(seed.name, seed.links.first().uri) },
                onMultiLink = { showContent = !showContent },
                seed = seed,
                showContent = showContent
            )
            if (widthSizeClass == WindowWidthSizeClass.Compact) {
                ExpandableMenu(
                    showMenu = showMenu,
                    onToggleMenu = { showMenu = !showMenu },
                    onEdit = { callbacks.onEdit(seed) },
                    onDelete = { callbacks.onDelete(seed) }
                )
            }
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
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isSingleLink: Boolean,
    onSingleLink: () -> Unit,
    onMultiLink: () -> Unit
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
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ){
            SeedInfo(seed = seed)
        }
        TrailButtons(
            showContent = showContent,
            isSingleLink = isSingleLink,
            onSingleLink = onSingleLink,
            onMultiLink = onMultiLink
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
    seed: LinkSeed
){
    val isSingleLink by remember(seed.links.size) {
        mutableStateOf(seed.links.size == 1)
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (isSingleLink) {
            SeedInfoSingleLink(seed = seed)
        } else {
            SeedInfoMultiLink(seed = seed)
        }
    }
}

@Composable
private fun SeedInfoSingleLink(
    seed: LinkSeed
){
    if (seed.links.isNotEmpty()) {
        SelectionContainer{
            Text(
                text = seed.links.first().toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

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

@Composable
private fun SeedInfoMultiLink(
    seed: LinkSeed
){

    if (seed.links.isNotEmpty()) {
        Text(
            text = "Links: ${seed.links.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

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

@Composable
private fun TrailButtons(
    modifier: Modifier = Modifier,
    isSingleLink: Boolean,
    onSingleLink: () -> Unit,
    showContent: Boolean = false,
    onMultiLink: () -> Unit
) {
    val (iconRes, contentDescription, onClick) = when {
        isSingleLink ->
            Triple(R.drawable.round_content_copy_24, "Copy link", onSingleLink)

        showContent ->
            Triple(R.drawable.round_unfold_less_24, "Hide more links", onMultiLink)

        else ->
            Triple(R.drawable.round_unfold_more_24, "Show more links", onMultiLink)
    }

    val iconRotation by animateFloatAsState(
        targetValue = if (showContent) 180f else 0f,
        animationSpec = tween(ItemSeedDefaults.AnimationDuration),
        label = "Trail Icon Rotation"
    )

    Row(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClick
        ) {
            Icon(
                modifier = Modifier
                    .size(ItemSeedDefaults.IconSizeMedium)
                    .rotate(iconRotation),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
private fun ExpandableMenu(
    modifier: Modifier = Modifier,
    showMenu: Boolean,
    onToggleMenu: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val iconRotation by animateFloatAsState(
        targetValue = if (showMenu) 180f else 0f,
        animationSpec = tween(ItemSeedDefaults.AnimationDuration),
        label = "Menu Icon Rotation"
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn(animationSpec = tween(ItemSeedDefaults.AnimationDuration)),
            exit = fadeOut(animationSpec = tween(ItemSeedDefaults.AnimationDuration))
        ) {
            MenuButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
        Box(
            modifier = Modifier
                .height(25.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.extraSmall
                )
                .clickable(
                    indication = ripple(
                        bounded = true,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onToggleMenu
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.rotate(iconRotation),
                imageVector = ImageVector.vectorResource(R.drawable.baseline_expand_more_24),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun MenuButtons(
    modifier: Modifier = Modifier,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MenuButton(
            onClick = onEdit,
            label = "Edit",
            icon = Icons.Default.Edit,
            contentDescription = "Edit seed",
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        MenuButton(
            onClick = onDelete,
            label = "Delete",
            icon = Icons.Default.Delete,
            contentDescription = "Delete seed",
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun MenuButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String? = null,
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .height(ItemSeedDefaults.IconSizeMedium)
            .background(containerColor)
            .clickable(
                indication = ripple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ){
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.fillMaxHeight(),
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor
            )
            label?.let {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    modifier = Modifier,
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    maxLines = 1
                )
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
                    text = entry.toString(),
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