package com.habitiora.linkarium.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.relations.LinkGardenAggregate
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkGardenReadDao {

    @Query("SELECT COUNT(*) FROM ${DatabaseContract.LinkGarden.TABLE_NAME}")
    suspend fun count(): Int

    @Query("SELECT id FROM ${DatabaseContract.LinkGarden.TABLE_NAME}")
    suspend fun getAllIds(): List<Long>

    @Query("SELECT id FROM ${DatabaseContract.LinkGarden.TABLE_NAME}")
    fun getAllIdsFlow(): Flow<List<Long>>

    @Transaction
    @Query("SELECT * FROM ${DatabaseContract.LinkGarden.TABLE_NAME}")
    fun getAllGardens(): Flow<List<LinkGardenAggregate>>

    @Transaction
    @Query("""
        SELECT * FROM ${DatabaseContract.LinkGarden.TABLE_NAME}
        WHERE id = :gardenId
    """)
    fun getGardenById(gardenId: Long): Flow<LinkGardenAggregate?>

    @Transaction
    @Query("""
        SELECT * FROM ${DatabaseContract.LinkGarden.TABLE_NAME}
        ORDER BY id DESC
    """)
    suspend fun getAllGardensForExport(): List<LinkGardenAggregate>

    @Transaction
    @Query("""
        SELECT * FROM ${DatabaseContract.LinkGarden.TABLE_NAME}
        ORDER BY id DESC
    """)
    fun getAllGardensForExportFlow(): Flow<List<LinkGardenAggregate>>

    @Transaction
    @Query("""
        SELECT * FROM ${DatabaseContract.LinkGarden.TABLE_NAME}
        WHERE id IN (:gardenIds)
        ORDER BY id DESC
    """)
    suspend fun getGardensForExport(gardenIds: List<Long>): List<LinkGardenAggregate>

    @Transaction
    @Query("""
        SELECT * FROM ${DatabaseContract.LinkGarden.TABLE_NAME}
        WHERE id IN (:gardenIds)
        ORDER BY id DESC
    """)
    fun getGardensForExportFlow(gardenIds: List<Long>): Flow<List<LinkGardenAggregate>>
}
