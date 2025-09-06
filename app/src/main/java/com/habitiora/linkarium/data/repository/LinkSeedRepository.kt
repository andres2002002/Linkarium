package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface LinkSeedRepository {
    suspend fun insert(linkSeed: LinkSeed): Long
    suspend fun update(linkSeed: LinkSeed)
    suspend fun delete(linkSeed: LinkSeed)
    suspend fun deleteAll()
    fun getAll(): Flow<List<LinkSeed>>
    fun getById(id: Long): Flow<LinkSeed?>
    fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeed>>
    suspend fun deleteById(id: Long)
}