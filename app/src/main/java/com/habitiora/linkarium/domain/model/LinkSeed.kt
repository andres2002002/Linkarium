package com.habitiora.linkarium.domain.model

import android.net.Uri

data class LinkSeed(
    val name: String,
    val links: List<Uri>,
    val isFavorite: Boolean = false,
    val notes: String? = null,
    val tags: List<String> = emptyList(),
    val date: Long = System.currentTimeMillis()
)