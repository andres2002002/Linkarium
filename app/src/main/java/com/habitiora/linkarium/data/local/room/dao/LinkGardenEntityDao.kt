package com.habitiora.linkarium.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.domain.usecase.LinkGardenWithSeedsImpl
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkGardenEntityDao {
    companion object {
        const val TABLE_NAME = DatabaseContract.LinkGarden.TABLE_NAME
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(linkGarden: LinkGardenEntity): Long
    @Update
    suspend fun update(linkGarden: LinkGardenEntity)
    @Delete
    suspend fun delete(linkGarden: LinkGardenEntity)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()
    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<LinkGardenEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun getById(id: Long): Flow<LinkGardenEntity?>
    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    suspend fun deleteById(id: Long)

}