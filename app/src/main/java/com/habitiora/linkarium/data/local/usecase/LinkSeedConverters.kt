package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.model.LinkTag

fun LinkSeed.toEntity(): LinkSeedEntity = LinkSeedEntity(
    id = this.id,
    name = this.name,
    coverUri = this.coverUri?.toString(),
    gardenId = this.gardenId,
    order = this.order,
    isFavorite = this.isFavorite,
    notes = this.notes,
    modifiedAt = this.modifiedAt
)

fun List<LinkTag>.toTagEntities(newSeed: Long? = null): List<LinkTagEntity> =
    this.map { it.toEntity(newSeed) }

fun List<LinkEntry>.toEntryEntities(newSeed: Long? = null): List<LinkEntryEntity> =
    this.map { it.toEntity(newSeed) }

fun LinkTag.toEntity(newSeed: Long? = null): LinkTagEntity = LinkTagEntity(
    id = this.id,
    seedId = newSeed ?: this.seedId,
    tag = this.tag
)

fun LinkEntry.toEntity(newSeed: Long? = null): LinkEntryEntity = LinkEntryEntity(
    id = this.id,
    seedId = newSeed ?: this.seedId,
    uri = this.uri,
    label = this.label,
    note = this.note,
    order = this.order
)

fun LinkEntry.note(): String? = this.note?.ifBlank { null }
fun LinkEntry.label(): String? = this.label?.ifBlank { null }