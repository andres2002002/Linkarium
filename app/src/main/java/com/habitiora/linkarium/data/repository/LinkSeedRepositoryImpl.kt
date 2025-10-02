package com.habitiora.linkarium.data.repository

import androidx.room.withTransaction
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSource
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.data.local.usecase.toComplete
import com.habitiora.linkarium.data.local.usecase.toDomain
import com.habitiora.linkarium.data.local.usecase.toListEntryEntity
import com.habitiora.linkarium.data.local.usecase.toListTagEntity
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.model.LinkTag
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkSeedRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val linkSeedDataSource: LinkSeedDataSource,
    private val linkEntryDataSource: LinkEntryDataSource,
    private val linkTagDataSource: LinkTagDataSource,
    private val linkGardenDataSource: LinkGardenDataSource
): LinkSeedRepository {
    // Caché en memoria
    private val seedsCache = mutableMapOf<Long, LinkSeed>()

    override fun getAll(): Flow<List<LinkSeed>> =
        linkSeedDataSource.getAll().map { seeds ->
                seeds.map { seed ->
                    val links = linkEntryDataSource.getLinksBySeed(seed.id).first()
                    val tags = linkTagDataSource.getTagsBySeed(seed.id).first()
                    val linkSeed = seed.toDomain(links, tags)
                    seedsCache[linkSeed.id] = linkSeed
                    linkSeed
                }
            }

    // Obtener uno por ID con preferencia por caché
    override fun getById(id: Long): Flow<LinkSeed?> {
        val cached = seedsCache[id]
        return if (cached != null) {
            flowOf(cached)
        } else {
            linkSeedDataSource.getById(id)
                .map { seed ->
                    if (seed == null) return@map null
                    val links = linkEntryDataSource.getLinksBySeed(seed.id).first()
                    val tags = linkTagDataSource.getTagsBySeed(seed.id).first()
                    val linkSeed = seed.toDomain(links, tags)
                    linkSeed.also { seedsCache[it.id] = it }
                }
        }
    }

    override fun getSeedsByGarden(gardenId: Long): Flow<List<LinkSeed>> {
        // Primero intenta obtener desde el caché
        val cachedSeeds = seedsCache.values.filter { it.gardenId == gardenId }

        return if (cachedSeeds.isNotEmpty()) {
            // Si hay datos en caché para este jardín, los devuelve
            flowOf(cachedSeeds)
        } else {
            // Si no hay datos en caché, consulta el data source
            linkSeedDataSource.getSeedsByGarden(gardenId)
                .map { seeds ->
                    seeds.map {
                        val links = linkEntryDataSource.getLinksBySeed(it.id).first()
                        val tags = linkTagDataSource.getTagsBySeed(it.id).first()
                        val linkSeed = it.toDomain(links, tags)
                        seedsCache[linkSeed.id] = linkSeed
                        linkSeed
                    }
                }
        }
    }

    // Insertar con validaciones
    override suspend fun insert(linkSeed: LinkSeedImpl): Result<Long> =
        db.withTransaction {
            try {
                require(linkSeed.name.isNotBlank()) { "El nombre no puede estar vacío" }
                require(validateGarden(linkSeed.gardenId)) { "Garden not found" }
                val id = linkSeedDataSource.insert(linkSeed)
                require(validateSeed(id)) { "Seed not found" }

                val links = linkSeed.links.toListEntryEntity(id)
                val linksIds = linkEntryDataSource.insertAll(links)
                require(linksIds.size == links.size) { "Not all links were inserted" }

                require(handleTags(linkSeed.tags, id).isEmpty()) { "Error handling tags" }

                seedsCache[id] = linkSeed.copy(id = id)
                Timber.d("Seed inserted: $linkSeed")
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Actualizar con validaciones
    override suspend fun update(linkSeed: LinkSeed): Result<Unit> =
        db.withTransaction {
            try {
                require(linkSeed.id > 0) { "El ID debe ser válido" }
                require(linkSeed.name.isNotBlank()) { "El nombre no puede estar vacío" }
                require(validateGarden(linkSeed.gardenId)) { "Garden not found" }
                require(validateSeed(linkSeed.id)) { "Seed not found" }
                Timber.d("Updating seed: $linkSeed")
                linkSeedDataSource.update(linkSeed)

                require(
                    handleLinks(
                        linkSeed.links,
                        linkSeed.id
                    ).isEmpty()
                ) { "Error handling links" }
                require(handleTags(linkSeed.tags, linkSeed.id).isEmpty()) { "Error handling tags" }

                seedsCache[linkSeed.id] = linkSeed
                Timber.d("Seed updated: $linkSeed")
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private suspend fun validateGarden(gardenId: Long): Boolean {
        val garden = linkGardenDataSource.getById(gardenId).first()
        Timber.d("Garden found: $garden")
        return garden != null
    }

    private suspend fun validateSeed(seedId: Long): Boolean {
        val seed = linkSeedDataSource.getById(seedId).first()
        Timber.d("Seed found: $seed")
        return seed != null
    }

    private suspend fun handleTags(tags: List<LinkTag>, seedId: Long): List<LinkTagEntity> {
        val errorTags = mutableListOf<LinkTagEntity>()
        tags.toListTagEntity(seedId).forEach { tag ->
            try {
                val tagValid = linkTagDataSource.getById(tag.id).first()
                if (tagValid != null) {
                    linkTagDataSource.update(tag)
                } else {
                    linkTagDataSource.insert(tag)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling tag: $tag")
                errorTags.add(tag)
            }
        }
        Timber.d("Error tags: $errorTags")
        return errorTags
    }

    private suspend fun handleLinks(links: List<LinkEntry>, seedId: Long): List<LinkEntryEntity> {
        val errorLinks = mutableListOf<LinkEntryEntity>()
        links.toListEntryEntity(seedId).forEach { link ->
            try {
                val linkValid = linkEntryDataSource.getById(link.id).first()
                if (linkValid != null) {
                    linkEntryDataSource.update(link)
                } else {
                    linkEntryDataSource.insert(link)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling link: $link")
                errorLinks.add(link)
            }
        }
        Timber.d("Error links: $errorLinks")
        return errorLinks
    }

    // Eliminar
    override suspend fun delete(linkSeed: LinkSeed): Result<Unit> {
        return try {
            require(linkSeed.id > 0) { "El ID debe ser válido" }
            linkSeedDataSource.delete(linkSeed)
            seedsCache.remove(linkSeed.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteById(id: Long): Result<Unit> {
        return try {
            require(id > 0) { "El ID debe ser válido" }
            linkSeedDataSource.deleteById(id)
            seedsCache.remove(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Unit> {
        return try {
            linkSeedDataSource.deleteAll()
            seedsCache.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}