package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.usecase.toDomain
import com.habitiora.linkarium.data.local.usecase.toEntity
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkSeedRepositoryImp @Inject constructor(
    private val dao: LinkSeedEntityDao
): LinkSeedRepository {

    override suspend fun insert(linkSeed: LinkSeed): Long =
        dao.insert(linkSeed.toEntity())

    override suspend fun update(linkSeed: LinkSeed) =
        dao.update(linkSeed.toEntity())

    override suspend fun delete(linkSeed: LinkSeed) =
        dao.delete(linkSeed.toEntity())

    override suspend fun deleteById(id: Long) = dao.deleteById(id)

    override suspend fun deleteAll() = dao.deleteAll()

    override fun getAll(): Flow<List<LinkSeed>> =
        dao.getAll()

    override fun getById(id: Long): Flow<LinkSeed?> =
        dao.getById(id)

    override fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeed>> =
        dao.getSeedsByGarden(gardenId)

}