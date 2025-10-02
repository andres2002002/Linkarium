package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import kotlinx.coroutines.flow.Flow

interface LinkGardenDataSource {
    suspend fun insert(linkGarden: LinkGarden): Long
    suspend fun update(linkGarden: LinkGarden)
    suspend fun delete(linkGarden: LinkGarden)
    suspend fun deleteAll()
    fun getAll(): Flow<List<LinkGarden>>
    fun getById(id: Long): Flow<LinkGarden?>
    suspend fun deleteById(id: Long)

}