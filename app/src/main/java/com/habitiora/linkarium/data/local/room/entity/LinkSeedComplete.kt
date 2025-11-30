package com.habitiora.linkarium.data.local.room.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl

data class LinkSeedComplete(
    @Embedded val seed: LinkSeedEntity,
    @Relation(
        parentColumn = DatabaseContract.LinkSeed.COLUMN_ID,
        entityColumn = DatabaseContract.LinkEntry.COLUMN_SEED_ID
    )
    val links: List<LinkEntryEntity>,

    @Relation(
        parentColumn = DatabaseContract.LinkSeed.COLUMN_ID,
        entityColumn = DatabaseContract.LinkTag.COLUMN_SEED_ID
    )
    val tags: List<LinkTagEntity>
){
    fun toDomain(): LinkSeed =
        LinkSeedImpl(
            id = seed.id,
            name = seed.name,
            links = links,
            gardenId = seed.gardenId,
            order = seed.order,
            isFavorite = seed.isFavorite,
            notes = seed.notes,
            tags = tags,
            modifiedAt = seed.modifiedAt
        )
}