package com.habitiora.linkarium.data.local.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import kotlinx.serialization.Serializable

@Serializable
data class LinkGardenAggregate(
    @Embedded val garden: LinkGardenEntity,

    @Relation(
        entity = LinkSeedEntity::class,
        parentColumn = DatabaseContract.LinkGarden.COLUMN_ID,
        entityColumn = DatabaseContract.LinkSeed.COLUMN_GARDEN_ID
    )
    val seeds: List<LinkSeedAggregate>
)
