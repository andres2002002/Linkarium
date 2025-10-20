package com.habitiora.linkarium.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkEntryEntityDao {
    companion object {
        const val TABLE_NAME = DatabaseContract.LinkEntry.TABLE_NAME
    }
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(linkEntry: LinkEntryEntity): Long
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(linkEntries: List<LinkEntryEntity>): List<Long>
    @Update
    suspend fun update(linkEntry: LinkEntryEntity)
    @Delete
    suspend fun delete(linkEntry: LinkEntryEntity)
    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()
    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<LinkEntryEntity>>
    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<LinkEntryEntity?>
    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    suspend fun deleteById(id: Long)
    @Query("SELECT * FROM $TABLE_NAME WHERE seed_id = :seedId")
    fun getBySeedId(seedId: Long): Flow<List<LinkEntryEntity>>
}