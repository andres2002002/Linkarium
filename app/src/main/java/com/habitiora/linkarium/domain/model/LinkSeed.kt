package com.habitiora.linkarium.domain.model

import android.net.Uri
import java.time.LocalDateTime

interface LinkSeed{
    val id: Long
    val name: String
    val links: List<Uri>
    val collection: Long
    val isFavorite: Boolean
    val notes: String?
    val tags: List<String>
    val modifiedAt: LocalDateTime

    fun update(
        id: Long = this.id,
        name: String = this.name,
        links: List<Uri> = this.links,
        collection: Long = this.collection,
        isFavorite: Boolean = this.isFavorite,
        notes: String? = this.notes,
        tags: List<String> = this.tags,
        modifiedAt: LocalDateTime = this.modifiedAt
    ): LinkSeed
}