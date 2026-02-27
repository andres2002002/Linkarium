package com.habitiora.linkarium.data.local.room.relations

import androidx.core.net.toUri
import androidx.room.Embedded
import androidx.room.Relation
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import kotlinx.serialization.Serializable

@Serializable
data class LinkSeedAggregate(
    @Embedded val seed: LinkSeedEntity,

    @Relation(
        entity = LinkEntryEntity::class,
        parentColumn = DatabaseContract.LinkSeed.COLUMN_ID,
        entityColumn = DatabaseContract.LinkEntry.COLUMN_SEED_ID
    )
    val links: List<LinkEntryEntity>,

    @Relation(
        entity = LinkTagEntity::class,
        parentColumn = DatabaseContract.LinkSeed.COLUMN_ID,
        entityColumn = DatabaseContract.LinkTag.COLUMN_SEED_ID
    )
    val tags: List<LinkTagEntity>
){
    fun toDomain(): LinkSeed = LinkSeedImpl(
        id = seed.id,
        name = seed.name,
        order = seed.order,
        coverUri = seed.coverUri?.toUri(),
        isFavorite = seed.isFavorite,
        notes = seed.notes,
        gardenId = seed.gardenId,
        links = links,
        tags = tags,
        modifiedAt = seed.modifiedAt
    )
}
