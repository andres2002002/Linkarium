package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedComplete
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.model.LinkTag
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl

fun LinkSeed.toEntity(): LinkSeedEntity = LinkSeedEntity(
    id = this.id,
    name = this.name,
    gardenId = this.gardenId,
    isFavorite = this.isFavorite,
    notes = this.notes,
    modifiedAt = this.modifiedAt
)

fun LinkSeed.toComplete(): LinkSeedComplete {
    val seedEntity = this.toEntity()
    val tags: List<LinkTagEntity> = this.tags.toListTagEntity()
    val links: List<LinkEntryEntity> = this.links.toListEntryEntity()
    return LinkSeedComplete(
        seed = seedEntity,
        tags = tags,
        links = links
    )
}

fun LinkSeedEntity.toDomain(
    links: List<LinkEntryEntity> = emptyList(),
    tags: List<LinkTagEntity> = emptyList()
): LinkSeed = LinkSeedImpl(
    id = this.id,
    name = this.name,
    gardenId = this.gardenId,
    isFavorite = this.isFavorite,
    notes = this.notes,
    modifiedAt = this.modifiedAt,
    links = links,
    tags = tags
)


fun List<LinkSeedComplete>.toListDomain(): List<LinkSeed> =
    this.map { it.toDomain() }

fun List<LinkTag>.toListTagEntity(newSeed: Long? = null): List<LinkTagEntity> =
    this.map { it.toEntity(newSeed) }

fun List<LinkEntry>.toListEntryEntity(newSeed: Long? = null): List<LinkEntryEntity> =
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
    note = this.note
)

fun LinkEntry.note(): String? = this.note?.ifBlank { null }
fun LinkEntry.label(): String? = this.label?.ifBlank { null }