package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.dao.LinkEntryEntityDao
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.usecase.toEntity
import com.habitiora.linkarium.domain.model.LinkEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkEntryDataSourceImpl @Inject constructor(
    private val dao: LinkEntryEntityDao
): LinkEntryDataSource {
    override suspend fun insert(linkEntry: LinkEntry): Long =
        dao.insert(linkEntry.toEntity())
    override suspend fun insertAll(linkEntries: List<LinkEntry>): List<Long> =
        dao.insertAll(linkEntries.map { it.toEntity() })
    override suspend fun update(linkEntry: LinkEntry) =
        dao.update(linkEntry.toEntity())
    override suspend fun delete(linkEntry: LinkEntry) =
        dao.delete(linkEntry.toEntity())

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)
    override suspend fun deleteAll() =
        dao.deleteAll()
    override fun getAll(): Flow<List<LinkEntryEntity>> =
        dao.getAll()
    override fun getById(id: Long): Flow<LinkEntryEntity?> =
        dao.getById(id)

    override fun getLinksBySeed(seedId: Long): Flow<List<LinkEntryEntity>> =
        dao.getBySeedId(seedId)
}