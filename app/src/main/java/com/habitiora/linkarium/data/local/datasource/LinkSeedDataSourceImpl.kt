package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.local.room.entity.LinkSeedComplete
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.usecase.toEntity
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkSeedDataSourceImpl @Inject constructor(
    private val dao: LinkSeedEntityDao
): LinkSeedDataSource {

    override suspend fun insert(linkSeed: LinkSeed): Long =
        dao.insert(linkSeed.toEntity())

    override suspend fun update(linkSeed: LinkSeed) =
        dao.update(linkSeed.toEntity())

    override suspend fun delete(linkSeed: LinkSeed) =
        dao.delete(linkSeed.toEntity())

    override suspend fun deleteById(id: Long) = dao.deleteById(id)

    override suspend fun deleteAll() = dao.deleteAll()

    override fun getAll(): Flow<List<LinkSeedEntity>> =
        dao.getAll()

    override fun getById(id: Long): Flow<LinkSeedEntity?> =
        dao.getById(id)

    override fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeedEntity>> =
        dao.getSeedsByGarden(gardenId)
}