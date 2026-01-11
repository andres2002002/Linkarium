package com.habitiora.linkarium.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.relations.LinkGardenAggregate
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkGardenReadDao {

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
    fun getGardensForExport(): List<LinkGardenAggregate>
}
