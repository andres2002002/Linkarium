package com.habitiora.linkarium.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkTagEntityDao {
    companion object {
        const val TABLE_NAME = DatabaseContract.LinkTag.TABLE_NAME
    }
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(linkTag: LinkTagEntity): Long
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(linkTags: List<LinkTagEntity>): List<Long>
    @Update
    suspend fun update(linkTag: LinkTagEntity)
    @Delete
    suspend fun delete(linkTag: LinkTagEntity)
    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()
    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<LinkTagEntity>>
    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<LinkTagEntity?>
    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    suspend fun deleteById(id: Long)
    @Query("SELECT * FROM $TABLE_NAME WHERE seed_id = :seedId")
    fun getBySeedId(seedId: Long): Flow<List<LinkTagEntity>>
}