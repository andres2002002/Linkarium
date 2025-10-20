package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.domain.model.LinkTag
import kotlinx.coroutines.flow.Flow

interface LinkTagDataSource {
    suspend fun insert(linkTag: LinkTag): Long
    suspend fun insertAll(linkTags: List<LinkTag>): List<Long>
    suspend fun update(linkTag: LinkTag)
    suspend fun delete(linkTag: LinkTag)
    suspend fun deleteById(id: Long)
    suspend fun deleteAll()
    fun getAll(): Flow<List<LinkTagEntity>>
    fun getById(id: Long): Flow<LinkTagEntity?>
    fun getTagsBySeed(seedId: Long): Flow<List<LinkTagEntity>>
}