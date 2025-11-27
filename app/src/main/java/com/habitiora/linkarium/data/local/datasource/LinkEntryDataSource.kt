package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.domain.model.LinkEntry
import kotlinx.coroutines.flow.Flow

interface LinkEntryDataSource {
    suspend fun insert(linkEntry: LinkEntry): Long
    suspend fun insertAll(linkEntries: List<LinkEntry>): List<Long>
    suspend fun update(linkEntry: LinkEntry)
    suspend fun updateAll(linkEntries: List<LinkEntry>)
    suspend fun delete(linkEntry: LinkEntry)
    suspend fun deleteAll(linkEntries: List<LinkEntry>)
    suspend fun deleteById(id: Long)
    suspend fun deleteAll()
    fun getAll(): Flow<List<LinkEntryEntity>>
    fun getById(id: Long): Flow<LinkEntryEntity?>
    fun getBySeedUrl(seedId: Long, url: String): Flow<LinkEntryEntity?>
    fun getLinksBySeed(seedId: Long): Flow<List<LinkEntryEntity>>
}