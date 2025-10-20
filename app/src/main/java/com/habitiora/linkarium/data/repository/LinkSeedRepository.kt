package com.habitiora.linkarium.data.repository

import androidx.paging.PagingData
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LinkSeedRepository {
    fun getAll(): Flow<List<LinkSeed>>
    fun getById(id: Long): Flow<LinkSeed?>
    fun getSeedsByGarden(gardenId: Long): Flow<PagingData<LinkSeed>>

    suspend fun insert(linkSeed: LinkSeedImpl): Result<Long>
    suspend fun update(linkSeed: LinkSeed): Result<Unit>
    suspend fun delete(linkSeed: LinkSeed): Result<Unit>

    suspend fun deleteById(id: Long): Result<Unit>
    suspend fun deleteAll(): Result<Unit>
}