package com.habitiora.linkarium.data.local.room

import androidx.core.net.toUri
import com.habitiora.linkarium.domain.usecase.LinkGardenWithSeedsImpl
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl

object DatabaseContract {
    const val DATABASE_NAME = "app_database"

    object LinkSeed {
        const val TABLE_NAME = "link_seed"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_GARDEN_ID = "gardenId"
        const val COLUMN_IS_FAVORITE = "isFavorite"
        const val COLUMN_ORDER = "sort_order"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_DATE_TIME = "date_time"

        val Empty = LinkSeedImpl()
    }
    object LinkGarden {
        const val TABLE_NAME = "link_garden"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_ORDER = "sort_order"
        val Empty = LinkGardenEntity(
            name = "",
            description = ""
        )
    }

    object LinkEntry {
        const val TABLE_NAME = "link_entry"
        const val COLUMN_ID = "id"
        const val COLUMN_SEED_ID = "seed_id"
        const val COLUMN_ORDER = "sort_order"
        const val COLUMN_URI = "uri"
        const val COLUMN_LABEL = "label"
        const val COLUMN_NOTE = "note"
        val Empty = LinkEntryEntity(
            seedId = 0,
            uri = "".toUri(),
            label = "",
            note = ""
        )
    }

    object LinkTag {
        const val TABLE_NAME = "link_tag"
        const val COLUMN_ID = "id"
        const val COLUMN_SEED_ID = "seed_id"
        const val COLUMN_TAG = "tag"
        val Empty = LinkTagEntity(
            seedId = 0,
            tag = ""
        )
    }

    object LinkGardenWithSeeds {
        val Empty = LinkGardenWithSeedsImpl(
            garden = LinkGardenEntity(0, "", ""),
            seeds = emptyList()
        )
    }
}
