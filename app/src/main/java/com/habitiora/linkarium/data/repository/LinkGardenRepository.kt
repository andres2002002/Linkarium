package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import kotlinx.coroutines.flow.Flow

interface LinkGardenRepository {
    suspend fun insert(linkGarden: LinkGarden): Long
    suspend fun update(linkGarden: LinkGarden)
    suspend fun delete(linkGarden: LinkGarden)
    suspend fun deleteAll()
    fun getAll(): Flow<List<LinkGarden>>
    fun getById(id: Long): Flow<LinkGarden?>
    fun getGardenWithSeeds(gardenId: Long): Flow<LinkGardenWithSeeds>
    suspend fun deleteById(id: Long)

}