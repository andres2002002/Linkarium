package com.habitiora.linkarium.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkSeedEntityDao {
    companion object {
        const val TABLE_NAME = DatabaseContract.LinkSeed.TABLE_NAME
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(linkSeed: LinkSeedEntity): Long
    @Update
    suspend fun update(linkSeed: LinkSeedEntity)
    @Delete
    suspend fun delete(linkSeed: LinkSeedEntity)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()
    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<LinkSeedEntity>>
    @Query("SELECT * FROM $TABLE_NAME WHERE collection = :gardenId")
    fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeedEntity>>
    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<LinkSeedEntity?>
    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    suspend fun deleteById(id: Long)
}