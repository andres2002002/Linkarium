package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow

interface LinkSeedRepository {
    fun getAll(): Flow<List<LinkSeed>>
    fun getById(id: Long): Flow<LinkSeed?>
    fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeed>>
    suspend fun insert(linkSeed: LinkSeed): Result<Long>
    suspend fun update(linkSeed: LinkSeed): Result<Unit>
    suspend fun delete(linkSeed: LinkSeed): Result<Unit>
    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}