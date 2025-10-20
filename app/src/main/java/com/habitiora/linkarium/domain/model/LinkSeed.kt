package com.habitiora.linkarium.domain.model

import java.time.LocalDateTime

interface LinkSeed{
    val id: Long
    val name: String
    val links: List<LinkEntry>
    val gardenId: Long
    val isFavorite: Boolean
    val notes: String?
    val tags: List<LinkTag>
    val modifiedAt: LocalDateTime
}