package com.habitiora.linkarium.ui.screens.showGarden

import com.habitiora.linkarium.domain.model.LinkSeed

data class ItemSeedCallbacks(
    val onDoubleTap: () -> Unit,
    val onLongPress: () -> Unit,
    val onEdit: (LinkSeed) -> Unit,
    val onDelete: (LinkSeed) -> Unit,
    val onCheckedChange: (Boolean) -> Unit
)