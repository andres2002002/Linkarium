package com.habitiora.linkarium.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSource
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.dao.LinkSeedReadDao
import com.habitiora.linkarium.data.local.usecase.toEntryEntities
import com.habitiora.linkarium.data.local.usecase.toTagEntities
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkSeedRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val linkSeedReadDao: LinkSeedReadDao,
    private val linkSeedDataSource: LinkSeedDataSource,
    private val linkEntryDataSource: LinkEntryDataSource,
    private val linkTagDataSource: LinkTagDataSource
): LinkSeedRepository {

    companion object {
        private const val PAGE_SIZE = 30
        private val PAGING_CONFIG = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = PAGE_SIZE / 2,
            initialLoadSize = PAGE_SIZE * 2
        )
    }

    // ---------------------------------------------------------
    // READ - Con estrategia cache-first
    // ---------------------------------------------------------

    override fun getAll(): Flow<List<LinkSeed>> =
        linkSeedReadDao.getAll().map { list -> list.map { it.toDomain() } }
    
    override fun getById(id: Long): Flow<LinkSeed?> =
        linkSeedReadDao.getById(id).map { it?.toDomain() }

    override fun getSeedsByGarden(gardenId: Long): Flow<PagingData<LinkSeed>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { linkSeedReadDao.getByGarden(gardenId) }
        ).flow.map { paging ->
            paging.map { entity -> entity.toDomain() }
        }

    // ---------------------------------------------------------
    // WRITE - Con sincronización de caché
    // ---------------------------------------------------------

    override suspend fun insert(linkSeed: LinkSeedImpl): Result<Long> =
        runCatching {
            db.withTransaction {
                val maxOrder = linkSeedDataSource.getMaxOrder(linkSeed.gardenId)
                val seedId = linkSeedDataSource.insert(linkSeed.copy(order = maxOrder + 1))

                linkEntryDataSource.upsertAll(linkSeed.links.toEntryEntities(seedId))
                linkTagDataSource.upsertAll(linkSeed.tags.toTagEntities(seedId))

                seedId
            }
        }

    override suspend fun update(linkSeed: LinkSeed): Result<Unit> =
        runCatching {
            db.withTransaction {
                linkSeedDataSource.update(linkSeed)

                val entries = linkSeed.links.toEntryEntities(linkSeed.id)
                linkEntryDataSource.upsertAll(entries)
                linkEntryDataSource.deleteMissing(linkSeed.id, entries.map { it.id })

                val tags = linkSeed.tags.toTagEntities(linkSeed.id)
                linkTagDataSource.upsertAll(tags)
            }
        }

    // ---------------------------------------------------------
    // DELETE - Con limpieza de caché
    // ---------------------------------------------------------

    override suspend fun delete(linkSeed: LinkSeed): Result<Unit> =
        runCatching { linkSeedDataSource.delete(linkSeed) }

    override suspend fun deleteById(id: Long): Result<Unit> =
        runCatching { linkSeedDataSource.deleteById(id) }

    override suspend fun deleteAll(): Result<Unit> =
        runCatching { linkSeedDataSource.deleteAll() }
}