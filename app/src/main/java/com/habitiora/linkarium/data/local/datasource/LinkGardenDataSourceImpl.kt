package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.dao.LinkGardenEntityDao
import com.habitiora.linkarium.data.local.usecase.toEntity
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class LinkGardenDataSourceImpl @Inject constructor(
    private val dao: LinkGardenEntityDao
): LinkGardenDataSource {
    override suspend fun insert(linkGarden: LinkGarden): Long =
        dao.insert(linkGarden.toEntity())

    override suspend fun update(linkGarden: LinkGarden) =
        dao.update(linkGarden.toEntity())

    override suspend fun delete(linkGarden: LinkGarden) =
        dao.delete(linkGarden.toEntity())

    override suspend fun deleteAll() =
        dao.deleteAll()

    override fun getAll(): Flow<List<LinkGarden>> =
        dao.getAll()

    override fun getById(id: Long): Flow<LinkGarden?> =
        dao.getById(id)

    override suspend fun deleteById(id: Long) =
        dao.deleteById(id)
}
