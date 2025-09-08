package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkGardenRepositoryImpl @Inject constructor(
    private val gardenDataSource: LinkGardenDataSource
): LinkGardenRepository {
    // Caché en memoria
    private val gardensCache = mutableMapOf<Long, LinkGarden>()

    private val gardenWithSeedsCache = mutableMapOf<Long, LinkGardenWithSeeds>()

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

    override fun getGardenWithSeeds(gardenId: Long): Flow<LinkGardenWithSeeds?> {
        val cached = gardenWithSeedsCache[gardenId]
        return if (cached != null) {
            flowOf(cached)
        } else {
            gardenDataSource.getGardenWithSeeds(gardenId).map { gardenWithSeeds ->
                gardenWithSeeds?.also { gardenWithSeedsCache[gardenId] = it }
            }
        }
    }

    // Insertar con validaciones
    override suspend fun insert(linkGarden: LinkGarden): Result<Long> {
        if (linkGarden.name.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        val id = gardenDataSource.insert(linkGarden)
        gardensCache[id] = linkGarden.update(id = id)
        return Result.success(id)
    }

    // Actualizar con validaciones
    override suspend fun update(linkGarden: LinkGarden): Result<Unit> {
        if (linkGarden.id <= 0) {
            return Result.failure(IllegalArgumentException("El ID debe ser válido"))
        }
        if (linkGarden.name.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre no puede estar vacío"))
        }
        gardenDataSource.update(linkGarden)
        gardensCache[linkGarden.id] = linkGarden
        gardenWithSeedsCache.remove(linkGarden.id)
        return Result.success(Unit)
    }

    // Eliminar
    override suspend fun delete(linkGarden: LinkGarden): Result<Unit> {
        if (linkGarden.id <= 0) {
            return Result.failure(IllegalArgumentException("El ID debe ser válido"))
        }
        gardenDataSource.delete(linkGarden)
        gardensCache.remove(linkGarden.id)
        gardenWithSeedsCache.remove(linkGarden.id)
        return Result.success(Unit)
    }

    override suspend fun deleteById(id: Long): Result<Unit> {
        if (id <= 0) {
            return Result.failure(IllegalArgumentException("El ID debe ser válido"))
        }
        gardenDataSource.deleteById(id)
        gardensCache.remove(id)
        gardenWithSeedsCache.remove(id)
        return Result.success(Unit)
    }

    override suspend fun deleteAll(): Result<Unit> {
        gardenDataSource.deleteAll()
        gardensCache.clear()
        gardenWithSeedsCache.clear()
        return Result.success(Unit)
    }
}