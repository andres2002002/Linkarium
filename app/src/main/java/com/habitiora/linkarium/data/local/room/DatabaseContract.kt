package com.habitiora.linkarium.data.local.room

import com.habitiora.linkarium.data.local.room.entity.GardenWithSeeds
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity

object DatabaseContract {
    const val DATABASE_NAME = "app_database"

    object LinkSeed {
        const val TABLE_NAME = "link_seed"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_LINKS = "links"
        const val COLUMN_COLLECTION = "collection"
        const val COLUMN_IS_FAVORITE = "isFavorite"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_TAGS = "tags"
        const val COLUMN_DATE_TIME = "date_time"

        val Empty = LinkSeedEntity(
            name = "",
            links = emptyList()
        )
    }
    object LinkGarden {
        const val TABLE_NAME = "link_garden"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        val Empty = LinkGardenEntity(
            name = "",
            description = ""
        )
    }

    object LinkGardenWithSeeds {
        val Empty = GardenWithSeeds(
            garden = LinkGardenEntity(0, "", ""),
            seeds = emptyList()
        )
    }
}
