package com.habitiora.linkarium.data.local.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.habitiora.linkarium.data.local.room.DatabaseContract

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // ============================
        // 1. Añadir 'sort_order' a link_garden
        // ============================
        db.execSQL("""
            ALTER TABLE ${DatabaseContract.LinkGarden.TABLE_NAME}
            ADD COLUMN ${DatabaseContract.LinkGarden.COLUMN_ORDER} INTEGER NOT NULL DEFAULT 0
        """)

        db.execSQL("""
            UPDATE ${DatabaseContract.LinkGarden.TABLE_NAME}
            SET ${DatabaseContract.LinkGarden.COLUMN_ORDER} = ${DatabaseContract.LinkGarden.COLUMN_ID}
        """)

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_link_garden_sort_order 
            ON ${DatabaseContract.LinkGarden.TABLE_NAME}(${DatabaseContract.LinkGarden.COLUMN_ORDER})
        """)


        // ============================
        // 2. Añadir 'sort_order' a link_seed
        // ============================
        db.execSQL("""
            ALTER TABLE ${DatabaseContract.LinkSeed.TABLE_NAME}
            ADD COLUMN ${DatabaseContract.LinkSeed.COLUMN_ORDER} INTEGER NOT NULL DEFAULT 0
        """)

        db.execSQL("""
            UPDATE ${DatabaseContract.LinkSeed.TABLE_NAME}
            SET ${DatabaseContract.LinkSeed.COLUMN_ORDER} = ${DatabaseContract.LinkSeed.COLUMN_ID}
        """)

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_link_seed_order 
            ON ${DatabaseContract.LinkSeed.TABLE_NAME}(${DatabaseContract.LinkSeed.COLUMN_ORDER})
        """)


        // ============================
        // 3. Añadir 'sort_order' a link_entry
        // ============================
        db.execSQL("""
            ALTER TABLE ${DatabaseContract.LinkEntry.TABLE_NAME}
            ADD COLUMN ${DatabaseContract.LinkEntry.COLUMN_ORDER} INTEGER NOT NULL DEFAULT 0
        """)

        db.execSQL("""
            UPDATE ${DatabaseContract.LinkEntry.TABLE_NAME}
            SET ${DatabaseContract.LinkEntry.COLUMN_ORDER} = ${DatabaseContract.LinkEntry.COLUMN_ID}
        """)

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_link_entry_sort_order
            ON ${DatabaseContract.LinkEntry.TABLE_NAME}(${DatabaseContract.LinkEntry.COLUMN_ORDER})
        """)
    }
}

