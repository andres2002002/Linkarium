package com.habitiora.linkarium.data.repository

import androidx.room.withTransaction
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSource
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.entity.LinkSeedComplete
import com.habitiora.linkarium.data.local.usecase.toGardenWithSeeds
import com.habitiora.linkarium.data.local.usecase.toListDomain
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class LinkGardenRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val gardenDataSource: LinkGardenDataSource,
    private val seedDataSource: LinkSeedDataSource,
    private val entryDataSource: LinkEntryDataSource,
    private val tagDataSource: LinkTagDataSource
): LinkGardenRepository {
    // Caché en memoria
    private val gardensCache = mutableMapOf<Long, LinkGarden>()

    private val gardenWithSeedsCache = mutableMapOf<Long, LinkGardenWithSeeds>()

    override fun clearCache() {
        gardensCache.clear()
        gardenWithSeedsCache.clear()
    }

    override fun clearCache(gardenId: Long) {
        gardensCache.remove(gardenId)
        gardenWithSeedsCache.remove(gardenId)
    }

    // Obtener todos los jardines con soporte de caché
    override fun getAll(): Flow<List<LinkGarden>> =
        gardenDataSource.getAll()
            .map { gardens ->
                // Actualiza caché en memoria
                gardens.forEach { gardensCache[it.id] = it }
                gardens
            }

    // Obtener uno por ID con preferencia por caché
    override fun getById(id: Long): Flow<LinkGarden?> {
        val cached = gardensCache[id]
        return if (cached != null) {
            flowOf(cached)
        } else {
            gardenDataSource.getById(id)
                .map { garden ->
                    garden?.also { gardensCache[it.id] = it }
                }
        }
    }

    private suspend fun getSeedsOrEmpty(gardenId: Long): List<LinkSeedComplete> =
        seedDataSource.getSeedsByGarden(gardenId).first().map { seed ->
            val links = entryDataSource.getLinksBySeed(seed.id).first()
            val tags = tagDataSource.getTagsBySeed(seed.id).first()
            LinkSeedComplete(seed, links, tags)
        }

    override suspend fun getGardenWithSeeds(gardenId: Long): LinkGardenWithSeeds? =
        db.withTransaction {
            val cached = gardenWithSeedsCache[gardenId]
            if (cached != null) {
                cached
            } else {
                val garden = gardenDataSource.getById(gardenId).first()
                val seeds = getSeedsOrEmpty(gardenId)

                val gardenWithSeeds = garden?.toGardenWithSeeds(seeds.toListDomain())
                gardenWithSeeds?.also { gardenWithSeedsCache[gardenId] = it }
            }
        }

    override suspend fun getAllWithSeeds(): List<LinkGardenWithSeeds> =
        db.withTransaction{
            val gardens = gardenDataSource.getAll().first()
            val gardensWithSeeds = gardens.map { garden ->
                val seeds = getSeedsOrEmpty(garden.id)
                garden.toGardenWithSeeds(seeds.toListDomain())
                    .also { gardenWithSeedsCache[garden.id] = it }
            }
            gardensWithSeeds
        }


    // Insertar con validaciones
    override suspend fun insert(linkGarden: LinkGarden): Result<Long> {
        try {
            require(linkGarden.name.isNotBlank()) { "El nombre no puede estar vacío" }
            require(linkGarden.id <= 0) { "El ID debe ser válido" }
            val id = gardenDataSource.insert(linkGarden)
            gardensCache[id] = linkGarden.update(id = id)
            return Result.success(id)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Actualizar con validaciones
    override suspend fun update(linkGarden: LinkGarden): Result<Unit> {
        try {
             require(linkGarden.id > 0) { "El ID debe ser válido" }
            require(linkGarden.name.isNotBlank()) { "El nombre no puede estar vacío" }
            gardenDataSource.update(linkGarden)
            gardensCache[linkGarden.id] = linkGarden
            gardenWithSeedsCache.remove(linkGarden.id)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Eliminar
    override suspend fun delete(linkGarden: LinkGarden): Result<Unit> {
        try {
            require(linkGarden.id > 0) { "El ID debe ser válido" }
            gardenDataSource.delete(linkGarden)
            gardensCache.remove(linkGarden.id)
            gardenWithSeedsCache.remove(linkGarden.id)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteById(id: Long): Result<Unit> {
        try {
            require(id > 0) { "El ID debe ser válido" }
            gardenDataSource.deleteById(id)
            gardensCache.remove(id)
            gardenWithSeedsCache.remove(id)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Unit> {
        try {
            gardenDataSource.deleteAll()
            gardensCache.clear()
            gardenWithSeedsCache.clear()
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}