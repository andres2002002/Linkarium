package com.habitiora.linkarium.data.local.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedComplete
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber

@Dao
interface LinkSeedEntityDao {
    companion object {
        const val TABLE_NAME = DatabaseContract.LinkSeed.TABLE_NAME
        const val COLUMN_ID = DatabaseContract.LinkSeed.COLUMN_ID
        const val COLUMN_GARDEN_ID = DatabaseContract.LinkSeed.COLUMN_GARDEN_ID
    }
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(linkSeed: LinkSeedEntity): Long
    @Update
    suspend fun update(linkSeed: LinkSeedEntity)
    @Delete
    suspend fun delete(linkSeed: LinkSeedEntity)
    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()
    @Query("DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = :id")
    suspend fun deleteById(id: Long)
    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<LinkSeedEntity>>
    @Query("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = :id")
    fun getById(id: Long): Flow<LinkSeedEntity?>
    @Query("SELECT * FROM $TABLE_NAME WHERE $COLUMN_GARDEN_ID = :gardenId ORDER BY id DESC")
    fun getSeedsByGarden(gardenId: Long): PagingSource<Int, LinkSeedEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE $COLUMN_GARDEN_ID = :gardenId")
    suspend fun getSeedsForExport(gardenId: Long): List<LinkSeedEntity>

    @Query("SELECT COALESCE(MAX(sort_order), -1) FROM $TABLE_NAME WHERE $COLUMN_GARDEN_ID = :gardenId")
    suspend fun getMaxOrder(gardenId: Long): Int
}