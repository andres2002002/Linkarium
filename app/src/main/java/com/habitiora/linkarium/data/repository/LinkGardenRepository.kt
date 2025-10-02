package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import kotlinx.coroutines.flow.Flow

interface LinkGardenRepository {

    fun clearCache()
    fun clearCache(gardenId: Long)
    fun getAll(): Flow<List<LinkGarden>>
    fun getById(id: Long): Flow<LinkGarden?>
    suspend fun getGardenWithSeeds(gardenId: Long): LinkGardenWithSeeds?
    suspend fun getAllWithSeeds(): List<LinkGardenWithSeeds>
    suspend fun insert(linkGarden: LinkGarden): Result<Long>
    suspend fun update(linkGarden: LinkGarden): Result<Unit>
    suspend fun delete(linkGarden: LinkGarden): Result<Unit>
    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}