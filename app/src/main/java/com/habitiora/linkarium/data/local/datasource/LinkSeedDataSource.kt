package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow

interface LinkSeedDataSource {
    suspend fun insert(linkSeed: LinkSeed): Long
    suspend fun update(linkSeed: LinkSeed)
    suspend fun delete(linkSeed: LinkSeed)
    suspend fun deleteAll()
    fun getAll(): Flow<List<LinkSeed>>
    fun getById(id: Long): Flow<LinkSeed?>
    fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeed>>
    suspend fun deleteById(id: Long)
}