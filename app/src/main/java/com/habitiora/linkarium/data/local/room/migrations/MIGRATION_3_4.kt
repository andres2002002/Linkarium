package com.habitiora.linkarium.data.local.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.habitiora.linkarium.data.local.room.DatabaseContract.LinkSeed
import com.habitiora.linkarium.data.local.room.DatabaseContract.LinkEntry

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Añadimos la columna coverUri a link_seeds
        db.execSQL("ALTER TABLE ${LinkSeed.TABLE_NAME} ADD COLUMN ${LinkSeed.COLUMN_COVER_URI} TEXT DEFAULT NULL")

        // Añadimos la columna thumbnailUri a link_entries
        db.execSQL("ALTER TABLE ${LinkEntry.TABLE_NAME} ADD COLUMN ${LinkEntry.COLUMN_THUMBNAIL_URI} TEXT DEFAULT NULL")
    }
}