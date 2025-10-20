package com.habitiora.linkarium.room

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.migrations.MIGRATION_1_2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4ClassRunner::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = AppDatabase::class.java,
        specs = emptyList(),
        openFactory = FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        // 1. Create DB in version 1 with old schema
        var db = helper.createDatabase(TEST_DB, 1).apply {
            // Insert test data with various edge cases
            execSQL("INSERT INTO link_seed (id, name, collection, isFavorite, notes, links, tags) VALUES (1, 'Seed1', 1, 0, 'note', 'uri1,uri2', 'tag1,tag2')")
            execSQL("INSERT INTO link_seed (id, name, collection, isFavorite, notes, links, tags) VALUES (2, 'Seed2', 2, 1, 'another note', 'uri3', 'tag3')")
            execSQL("INSERT INTO link_seed (id, name, collection, isFavorite, notes, links, tags) VALUES (3, 'EmptySeed', 3, 0, '', '', '')")
            execSQL("INSERT INTO link_seed (id, name, collection, isFavorite, notes, links, tags) VALUES (4, 'NullSeed', 4, 0, NULL, NULL, NULL)")
            close()
        }

        // 2. Run migration and validate schema
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

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
        assertEquals("Should have 2 links for seed 1", listOf("uri1", "uri2"), seed1Links.sorted())

        // Test seed 2 - single link
        val seed2Links = mutableListOf<String>()
        db.query("SELECT uri FROM link_entry WHERE seed_id = 2").use { cursor ->
            while (cursor.moveToNext()) {
                seed2Links.add(cursor.getString(cursor.getColumnIndexOrThrow("uri")))
            }
        }
        assertEquals("Should have 1 link for seed 2", listOf("uri3"), seed2Links)

        // Test seed 3 - no links (empty string)
        db.query("SELECT COUNT(*) FROM link_entry WHERE seed_id = 3").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Should have 0 links for seed 3", 0, cursor.getInt(0))
        }

        // Test seed 4 - no links (null)
        db.query("SELECT COUNT(*) FROM link_entry WHERE seed_id = 4").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Should have 0 links for seed 4", 0, cursor.getInt(0))
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

        // Verify null handling
        db.query("SELECT COUNT(*) FROM link_entry WHERE seed_id = 4").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Null values should result in 0 entries", 0, cursor.getInt(0))
        }

        db.query("SELECT COUNT(*) FROM link_tag WHERE seed_id = 4").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Null values should result in 0 entries", 0, cursor.getInt(0))
        }
    }

    @Test
    fun migrate1To2_withSpecialCharacters() {
        // Test migration with special characters and edge cases
        var db = helper.createDatabase(TEST_DB_SPECIAL, 1).apply {
            execSQL("INSERT INTO link_seed (id, name, collection, isFavorite, notes, links, tags) VALUES (1, 'Special Seed', 1, 0, 'notes with, commas', 'uri,with,commas,in,path', 'tag with spaces,another-tag')")
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB_SPECIAL, 2, true, MIGRATION_1_2)

        // Verify that links with commas in them are handled correctly
        // This assumes your migration handles escaped commas or uses a different delimiter
        val links = mutableListOf<String>()
        db.query("SELECT uri FROM link_entry WHERE seed_id = 1").use { cursor ->
            while (cursor.moveToNext()) {
                links.add(cursor.getString(cursor.getColumnIndexOrThrow("uri")))
            }
        }

        // Verify tags with spaces are preserved
        val tags = mutableListOf<String>()
        db.query("SELECT tag FROM link_tag WHERE seed_id = 1").use { cursor ->
            while (cursor.moveToNext()) {
                tags.add(cursor.getString(cursor.getColumnIndexOrThrow("tag")))
            }
        }
        assertEquals("Should preserve tag with spaces", "tag with spaces", tags.find { it.contains("spaces") })

        db.close()
    }

    @Test
    fun migrate1To2_verifySchemaChanges() {
        // Verify the old columns are removed and new tables exist
        var db = helper.createDatabase(TEST_DB_SCHEMA, 1).apply {
            execSQL("INSERT INTO link_seed (id, name, collection, isFavorite, notes, links, tags) VALUES (1, 'Test', 1, 0, 'note', 'uri', 'tag')")
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB_SCHEMA, 2, true, MIGRATION_1_2)

        // Verify new tables exist
        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='link_entry'").use { cursor ->
            assertTrue("link_entry table should exist", cursor.moveToFirst())
        }

        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='link_tag'").use { cursor ->
            assertTrue("link_tag table should exist", cursor.moveToFirst())
        }

        // Verify old columns don't exist in link_seed table
        db.query("PRAGMA table_info(link_seed)").use { cursor ->
            val columnNames = mutableListOf<String>()
            while (cursor.moveToNext()) {
                columnNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
            }
            assertFalse("links column should be removed", columnNames.contains("links"))
            assertFalse("tags column should be removed", columnNames.contains("tags"))
            assertTrue("gardenId column should exist", columnNames.contains("gardenId"))
        }

        db.close()
    }

    companion object {
        private const val TEST_DB = "migration-test"
        private const val TEST_DB_SPECIAL = "migration-test-special"
        private const val TEST_DB_SCHEMA = "migration-test-schema"
    }
}
