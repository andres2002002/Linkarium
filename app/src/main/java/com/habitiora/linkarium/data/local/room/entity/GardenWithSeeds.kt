package com.habitiora.linkarium.data.local.room.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds

data class GardenWithSeeds(
    @Embedded override val garden: LinkGardenEntity,
    @Relation(
        parentColumn = DatabaseContract.LinkGarden.COLUMN_ID,
        entityColumn = DatabaseContract.LinkSeed.COLUMN_COLLECTION
    )
    override val seeds: List<LinkSeedEntity>
): LinkGardenWithSeeds
