package com.habitiora.linkarium.data.local.datasource

import androidx.paging.Pager
import androidx.paging.PagingSource
import com.habitiora.linkarium.data.local.room.entity.LinkSeedComplete
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow

interface LinkSeedDataSource {
    suspend fun insert(linkSeed: LinkSeed): Long
    suspend fun update(linkSeed: LinkSeed)
    suspend fun delete(linkSeed: LinkSeed)

    suspend fun deleteAll()
    suspend fun deleteById(id: Long)

    fun getAll(): Flow<List<LinkSeedEntity>>
    fun getById(id: Long): Flow<LinkSeedEntity?>
    fun getSeedsByGarden(gardenId: Long): PagingSource<Int, LinkSeedEntity>
    suspend fun getSeedsForExport(gardenId: Long): List<LinkSeedEntity>
    suspend fun getMaxOrder(gardenId: Long): Int
}