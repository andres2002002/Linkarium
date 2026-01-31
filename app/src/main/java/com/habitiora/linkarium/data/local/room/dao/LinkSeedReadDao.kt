package com.habitiora.linkarium.data.local.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.relations.LinkSeedAggregate
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkSeedReadDao {

    @Transaction
    @Query("SELECT * FROM ${DatabaseContract.LinkSeed.TABLE_NAME}")
    fun getAll(): Flow<List<LinkSeedAggregate>>

    @Query("SELECT COUNT(*) FROM ${DatabaseContract.LinkSeed.TABLE_NAME}")
    suspend fun count(): Int

    @Transaction
    @Query("""
        SELECT * FROM ${DatabaseContract.LinkSeed.TABLE_NAME}
        WHERE gardenId = :gardenId
        ORDER BY id DESC
    """)
    fun getByGarden(gardenId: Long): PagingSource<Int, LinkSeedAggregate>

    @Transaction
    @Query("""
        SELECT * FROM ${DatabaseContract.LinkSeed.TABLE_NAME}
        WHERE id = :id
    """)
    fun getById(id: Long): Flow<LinkSeedAggregate?>
}
