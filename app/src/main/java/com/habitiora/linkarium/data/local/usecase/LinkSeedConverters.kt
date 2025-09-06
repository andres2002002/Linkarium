package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.domain.model.LinkSeed

fun LinkSeed.toEntity(): LinkSeedEntity = LinkSeedEntity(
    id = this.id,
    name = this.name,
    links = this.links,
    collection = this.collection,
    isFavorite = this.isFavorite,
    notes = this.notes,
    tags = this.tags,
    modifiedAt = this.modifiedAt
)

fun LinkSeedEntity.toDomain(): LinkSeed = this