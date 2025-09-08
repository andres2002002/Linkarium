package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkSeedRepositoryImpl @Inject constructor(
    private val linkSeedDataSource: LinkSeedDataSource
): LinkSeedRepository {
    // Caché en memoria
    private val seedsCache = mutableMapOf<Long, LinkSeed>()


    // Obtener todos los jardines con soporte de caché
    override fun getAll(): Flow<List<LinkSeed>> =
        linkSeedDataSource.getAll()
            .map { seeds ->
                // Actualiza caché en memoria
                seeds.forEach { seedsCache[it.id] = it }
                seeds
            }

    // Obtener uno por ID con preferencia por caché
    override fun getById(id: Long): Flow<LinkSeed?> {
        val cached = seedsCache[id]
        return if (cached != null) {
            flowOf(cached)
        } else {
            linkSeedDataSource.getById(id)
                .map { garden ->
                    garden?.also { seedsCache[it.id] = it }
                }
        }
    }

    override fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeed>> {
        // Primero intenta obtener desde el caché
        val cachedSeeds = seedsCache.values.filter { it.collection == gardenId }

        return if (cachedSeeds.isNotEmpty()) {
            // Si hay datos en caché para este jardín, los devuelve
            flowOf(cachedSeeds)
        } else {
            // Si no hay datos en caché, consulta el data source
            linkSeedDataSource.getSeedsByGarden(gardenId)
                .map { seeds ->
                    // Actualiza el caché con los datos obtenidos
                    seeds.forEach { seedsCache[it.id] = it }
                    seeds
                }
        }
    }

    // Insertar con validaciones
    override suspend fun insert(linkSeed: LinkSeed): Result<Long> {
        if (linkSeed.name.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        val id = linkSeedDataSource.insert(linkSeed)
        seedsCache[id] = linkSeed.update(id = id)
        return Result.success(id)
    }

    // Actualizar con validaciones
    override suspend fun update(linkSeed: LinkSeed): Result<Unit> {
        if (linkSeed.id <= 0) {
            return Result.failure(IllegalArgumentException("El ID debe ser válido"))
        }
        if (linkSeed.name.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        linkSeedDataSource.update(linkSeed)
        seedsCache[linkSeed.id] = linkSeed
        return Result.success(Unit)
    }

    // Eliminar
    override suspend fun delete(linkSeed: LinkSeed): Result<Unit> {
        if (linkSeed.id <= 0) {
            return Result.failure(IllegalArgumentException("El ID debe ser válido"))
        }
        linkSeedDataSource.delete(linkSeed)
        seedsCache.remove(linkSeed.id)
        return Result.success(Unit)
    }

    override suspend fun deleteById(id: Long): Result<Unit> {
        if (id <= 0) {
            return Result.failure(IllegalArgumentException("El ID debe ser válido"))
        }
        linkSeedDataSource.deleteById(id)
        seedsCache.remove(id)
        return Result.success(Unit)
    }

    override suspend fun deleteAll(): Result<Unit> {
        linkSeedDataSource.deleteAll()
        seedsCache.clear()
        return Result.success(Unit)
    }
}