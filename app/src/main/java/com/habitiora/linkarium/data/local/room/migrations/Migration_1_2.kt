package com.habitiora.linkarium.data.local.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.habitiora.linkarium.data.local.room.DatabaseContract

fun parseJsonArray(json: String): List<String> {
    return try {
        val type = object : TypeToken<List<String>>() {}.type
        Gson().fromJson<List<String>>(json, type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

const val LinkSeedNew = "link_seed_new"

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Crear tabla link_garden (si no existía antes)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS ${DatabaseContract.LinkGarden.TABLE_NAME} (
                ${DatabaseContract.LinkGarden.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${DatabaseContract.LinkGarden.COLUMN_NAME} TEXT NOT NULL,
                ${DatabaseContract.LinkGarden.COLUMN_DESCRIPTION} TEXT NOT NULL
            )
        """)

        // Crear índices en las tablas
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_garden_name ON ${DatabaseContract.LinkGarden.TABLE_NAME}(${DatabaseContract.LinkGarden.COLUMN_NAME})")

        // Quitar indices antiguos
        db.execSQL("DROP INDEX IF EXISTS index_link_seed_name")
        db.execSQL("DROP INDEX IF EXISTS index_link_seed_collection")
        db.execSQL("DROP INDEX IF EXISTS index_link_seed_gardenId")
        db.execSQL("DROP INDEX IF EXISTS index_link_seed_isFavorite")
        db.execSQL("DROP INDEX IF EXISTS index_link_seed_date_time")

        // Renombrar tabla vieja
        db.execSQL("ALTER TABLE ${DatabaseContract.LinkSeed.TABLE_NAME} RENAME TO link_seed_old")
        // 2. Crear tabla link_seed
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $LinkSeedNew (
                ${DatabaseContract.LinkSeed.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${DatabaseContract.LinkSeed.COLUMN_NAME} TEXT NOT NULL,
                ${DatabaseContract.LinkSeed.COLUMN_GARDEN_ID} INTEGER NOT NULL,
                ${DatabaseContract.LinkSeed.COLUMN_IS_FAVORITE} INTEGER NOT NULL,
                ${DatabaseContract.LinkSeed.COLUMN_NOTES} TEXT,
                ${DatabaseContract.LinkSeed.COLUMN_DATE_TIME} TEXT NOT NULL,
                FOREIGN KEY(${DatabaseContract.LinkSeed.COLUMN_GARDEN_ID}) 
                    REFERENCES ${DatabaseContract.LinkGarden.TABLE_NAME}(${DatabaseContract.LinkGarden.COLUMN_ID}) 
                    ON DELETE CASCADE ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_seed_name ON $LinkSeedNew(${DatabaseContract.LinkSeed.COLUMN_NAME})")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_seed_gardenId ON $LinkSeedNew(${DatabaseContract.LinkSeed.COLUMN_GARDEN_ID})")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_seed_isFavorite ON $LinkSeedNew(${DatabaseContract.LinkSeed.COLUMN_IS_FAVORITE})")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_seed_date_time ON $LinkSeedNew(${DatabaseContract.LinkSeed.COLUMN_DATE_TIME})")

        // 3. Crear tabla link_entry
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS ${DatabaseContract.LinkEntry.TABLE_NAME} (
                ${DatabaseContract.LinkEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${DatabaseContract.LinkEntry.COLUMN_SEED_ID} INTEGER NOT NULL,
                ${DatabaseContract.LinkEntry.COLUMN_URI} TEXT NOT NULL,
                ${DatabaseContract.LinkEntry.COLUMN_LABEL} TEXT,
                ${DatabaseContract.LinkEntry.COLUMN_NOTE} TEXT,
                FOREIGN KEY(${DatabaseContract.LinkEntry.COLUMN_SEED_ID}) 
                    REFERENCES ${DatabaseContract.LinkSeed.TABLE_NAME}(${DatabaseContract.LinkSeed.COLUMN_ID}) 
                    ON DELETE CASCADE ON UPDATE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_entry_seed_id ON ${DatabaseContract.LinkEntry.TABLE_NAME}(${DatabaseContract.LinkEntry.COLUMN_SEED_ID})")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_entry_label ON ${DatabaseContract.LinkEntry.TABLE_NAME}(${DatabaseContract.LinkEntry.COLUMN_LABEL})")

        // 4. Crear tabla link_tag
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS ${DatabaseContract.LinkTag.TABLE_NAME} (
                ${DatabaseContract.LinkTag.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                ${DatabaseContract.LinkTag.COLUMN_SEED_ID} INTEGER NOT NULL,
                ${DatabaseContract.LinkTag.COLUMN_TAG} TEXT NOT NULL,
                FOREIGN KEY(${DatabaseContract.LinkTag.COLUMN_SEED_ID}) 
                    REFERENCES ${DatabaseContract.LinkSeed.TABLE_NAME}(${DatabaseContract.LinkSeed.COLUMN_ID}) 
                    ON DELETE CASCADE ON UPDATE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_tag_seed_id ON ${DatabaseContract.LinkTag.TABLE_NAME}(${DatabaseContract.LinkTag.COLUMN_SEED_ID})")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_link_tag_tag ON ${DatabaseContract.LinkTag.TABLE_NAME}(${DatabaseContract.LinkTag.COLUMN_TAG})")

        // 5. Migrar datos desde la tabla antigua (linkseed vieja)
        val cursor = db.query("SELECT id, name, notes, links, tags, collection, date_time, isFavorite FROM link_seed_old")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val name = cursor.getString(1)
            val notes = cursor.getString(2)
            val linksJson = cursor.getString(3)
            val tagsJson = cursor.getString(4)
            val collection = cursor.getLong(5)
            val rawDateTime = cursor.getString(6)
            val dateTime = rawDateTime ?: "1970-01-01T00:00:00Z"
            val isFavorite = cursor.getInt(7)

            // Insertar semilla
            db.execSQL("""
                INSERT INTO $LinkSeedNew 
                (${DatabaseContract.LinkSeed.COLUMN_ID}, ${DatabaseContract.LinkSeed.COLUMN_NAME}, ${DatabaseContract.LinkSeed.COLUMN_GARDEN_ID}, ${DatabaseContract.LinkSeed.COLUMN_IS_FAVORITE}, ${DatabaseContract.LinkSeed.COLUMN_NOTES}, ${DatabaseContract.LinkSeed.COLUMN_DATE_TIME}) 
                VALUES (?, ?, ?, ?, ?, ?)
            """, arrayOf(id, name, collection, isFavorite, notes, dateTime))

            // Insertar links
            if (!linksJson.isNullOrEmpty()) {
                val links = parseJsonArray(linksJson)
                for (uri in links) {
                    db.execSQL("""
                        INSERT INTO ${DatabaseContract.LinkEntry.TABLE_NAME} 
                        (${DatabaseContract.LinkEntry.COLUMN_SEED_ID}, ${DatabaseContract.LinkEntry.COLUMN_URI}, ${DatabaseContract.LinkEntry.COLUMN_LABEL}, ${DatabaseContract.LinkEntry.COLUMN_NOTE}) 
                        VALUES (?, ?, ?, ?)
                    """, arrayOf(id, uri, null, null))
                }
            }

            // Insertar tags
            if (!tagsJson.isNullOrEmpty()) {
                val tags = parseJsonArray(tagsJson)
                for (tag in tags) {
                    db.execSQL("""
                        INSERT INTO ${DatabaseContract.LinkTag.TABLE_NAME} 
                        (${DatabaseContract.LinkTag.COLUMN_SEED_ID}, ${DatabaseContract.LinkTag.COLUMN_TAG}) 
                        VALUES (?, ?)
                    """, arrayOf(id, tag))
                }
            }
        }
        cursor.close()

        // 6. Borrar la tabla vieja
        db.execSQL("DROP TABLE link_seed_old")
        db.execSQL("ALTER TABLE $LinkSeedNew RENAME TO ${DatabaseContract.LinkSeed.TABLE_NAME}")
    }
}
