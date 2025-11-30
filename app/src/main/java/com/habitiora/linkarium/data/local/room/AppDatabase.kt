package com.habitiora.linkarium.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habitiora.linkarium.data.local.room.dao.LinkEntryEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkGardenEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkTagEntityDao
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity

@Database(
    entities = [
        LinkSeedEntity::class,
        LinkGardenEntity::class,
        LinkTagEntity::class,
        LinkEntryEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linkSeedDao(): LinkSeedEntityDao
    abstract fun linkGardenDao(): LinkGardenEntityDao
    abstract fun linkTagDao(): LinkTagEntityDao
    abstract fun linkEntryDao(): LinkEntryEntityDao
}
