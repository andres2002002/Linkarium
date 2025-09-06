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
}