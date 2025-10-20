package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.dao.LinkTagEntityDao
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.data.local.usecase.toEntity
import com.habitiora.linkarium.domain.model.LinkTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkTagDataSourceImpl @Inject constructor(
    private val dao: LinkTagEntityDao
): LinkTagDataSource {
    override suspend fun insert(linkTag: LinkTag): Long =
        dao.insert(linkTag.toEntity())
    override suspend fun insertAll(linkTags: List<LinkTag>): List<Long> =
        dao.insertAll(linkTags.map { it.toEntity() })
    override suspend fun update(linkTag: LinkTag) =
        dao.update(linkTag.toEntity())
    override suspend fun delete(linkTag: LinkTag) =
        dao.delete(linkTag.toEntity())

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)
    override suspend fun deleteAll() =
        dao.deleteAll()

    override fun getAll(): Flow<List<LinkTagEntity>> =
        dao.getAll()
    override fun getById(id: Long): Flow<LinkTagEntity?> =
        dao.getById(id)
    override fun getTagsBySeed(seedId: Long): Flow<List<LinkTagEntity>> =
        dao.getBySeedId(seedId)

}