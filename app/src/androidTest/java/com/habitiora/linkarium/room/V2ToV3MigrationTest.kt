package com.habitiora.linkarium.room

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.migrations.MIGRATION_1_2
import com.habitiora.linkarium.data.local.room.migrations.MIGRATION_2_3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class V2ToV3MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = AppDatabase::class.java,
        specs = emptyList(),
        openFactory = FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        // 1. Create DB in version 2 with old schema
        var db = helper.createDatabase(TEST_DB, 2).apply {
            // Insert test data with various edge cases
            execSQL("INSERT INTO link_garden (id, name, description) VALUES (1, 'Garden1', 'garden description')")
            execSQL("INSERT INTO link_seed (id, name, gardenId, isFavorite, notes, date_time) VALUES (1, 'Seed1', 1, 0, 'note', '2023-01-01 12:00:00')")
            execSQL("INSERT INTO link_seed (id, name, gardenId, isFavorite, notes, date_time) VALUES (2, 'Seed2', 2, 1, 'another note', '2023-02-01 15:30:00')")
            execSQL("INSERT INTO link_seed (id, name, gardenId, isFavorite, notes, date_time) VALUES (3, 'EmptySeed', 3, 0, '', '2023-03-01 09:45:00')")
            execSQL("INSERT INTO link_entry (id, seed_id, uri, note, label) VALUES (1, 1, 'www.google.com', 'entry note', 'entry label')")
            execSQL("INSERT INTO link_entry (id, seed_id, uri, note, label) VALUES (2, 1, 'www.youtube.com', 'entry note', 'entry label')")
            execSQL("INSERT INTO link_entry (id, seed_id, uri, note, label) VALUES (3, 2, 'www.google.con', 'entry note', 'entry label')")
            execSQL("INSERT INTO link_tag (id, seed_id, tag) VALUES (1, 1, 'tag1')")
            execSQL("INSERT INTO link_tag (id, seed_id, tag) VALUES (2, 1, 'tag2')")
            execSQL("INSERT INTO link_tag (id, seed_id, tag) VALUES (3, 2, 'tag3')" )
            close()
        }

        // 2. Run migration and validate schema
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        // 2.1 Verify that new 'sort_order' columns were created (link_garden, link_seed, link_entry)
        assertColumnExists(db, "link_garden", "sort_order")
        assertColumnExists(db, "link_seed", "sort_order")
        assertColumnExists(db, "link_entry", "sort_order")
        // 3. Verify main table data migration
        validateMainTableData(db)

        // 4. Verify links normalization
        validateLinksNormalization(db)

        // 5. Verify tags normalization
        validateTagsNormalization(db)

        // 6. Verify empty/null handling
        validateEmptyAndNullHandling(db)

        db.close()
    }

    private fun assertColumnExists(db: androidx.sqlite.db.SupportSQLiteDatabase, table: String, column: String) {
        db.query("PRAGMA table_info($table)").use { cursor ->
            var found = false
            while (cursor.moveToNext()) {
                val colName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                if (colName.equals(column, ignoreCase = true)) {
                    found = true
                    break
                }
            }
            assertTrue("Column '$column' should exist in table '$table'", found)
        }
    }

    private fun validateMainTableData(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.query("SELECT * FROM link_seed ORDER BY id").use { cursor ->
            // First record
            assertTrue("Should have first record", cursor.moveToFirst())
            assertEquals("Seed1", cursor.getString(cursor.getColumnIndexOrThrow("name")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("gardenId")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")))
            assertEquals("note", cursor.getString(cursor.getColumnIndexOrThrow("notes")))

            // Second record
            assertTrue("Should have second record", cursor.moveToNext())
            assertEquals("Seed2", cursor.getString(cursor.getColumnIndexOrThrow("name")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("gardenId")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")))
            assertEquals("another note", cursor.getString(cursor.getColumnIndexOrThrow("notes")))
        }
    }

    private fun validateLinksNormalization(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        // Test seed 1 - multiple links
        val seed1Links = mutableListOf<String>()
        db.query("SELECT uri FROM link_entry WHERE seed_id = 1 ORDER BY uri").use { cursor ->
            while (cursor.moveToNext()) {
                seed1Links.add(cursor.getString(cursor.getColumnIndexOrThrow("uri")))
            }
        }
        assertEquals("Should have 2 links for seed 1", listOf("www.google.com", "www.youtube.com"), seed1Links.sorted())

        // Test seed 2 - single link
        val seed2Links = mutableListOf<String>()
        db.query("SELECT uri FROM link_entry WHERE seed_id = 2").use { cursor ->
            while (cursor.moveToNext()) {
                seed2Links.add(cursor.getString(cursor.getColumnIndexOrThrow("uri")))
            }
        }
        assertEquals("Should have 1 link for seed 2", listOf("www.google.con"), seed2Links)

        // Test seed 3 - no links (empty string)
        db.query("SELECT COUNT(*) FROM link_entry WHERE seed_id = 3").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Should have 0 links for seed 3", 0, cursor.getInt(0))
        }
    }

    private fun validateTagsNormalization(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        // Test seed 1 - multiple tags
        val seed1Tags = mutableListOf<String>()
        db.query("SELECT tag FROM link_tag WHERE seed_id = 1 ORDER BY tag").use { cursor ->
            while (cursor.moveToNext()) {
                seed1Tags.add(cursor.getString(cursor.getColumnIndexOrThrow("tag")))
            }
        }
        assertEquals("Should have 2 tags for seed 1", listOf("tag1", "tag2"), seed1Tags.sorted())

        // Test seed 2 - single tag
        val seed2Tags = mutableListOf<String>()
        db.query("SELECT tag FROM link_tag WHERE seed_id = 2").use { cursor ->
            while (cursor.moveToNext()) {
                seed2Tags.add(cursor.getString(cursor.getColumnIndexOrThrow("tag")))
            }
        }
        assertEquals("Should have 1 tag for seed 2", listOf("tag3"), seed2Tags)
    }

    private fun validateEmptyAndNullHandling(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        // Verify empty string handling
        db.query("SELECT COUNT(*) FROM link_entry WHERE seed_id = 3").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Empty strings should result in 0 entries", 0, cursor.getInt(0))
        }

        db.query("SELECT COUNT(*) FROM link_tag WHERE seed_id = 3").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Empty strings should result in 0 entries", 0, cursor.getInt(0))
        }
    }

    companion object {
        private const val TEST_DB = "migration-test"
        private const val TEST_DB_SPECIAL = "migration-test-special"
        private const val TEST_DB_SCHEMA = "migration-test-schema"
    }
}
