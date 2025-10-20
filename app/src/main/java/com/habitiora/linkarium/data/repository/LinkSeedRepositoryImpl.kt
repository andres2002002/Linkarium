package com.habitiora.linkarium.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import androidx.room.withTransaction
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSource
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.data.local.usecase.toDomain
import com.habitiora.linkarium.data.local.usecase.toListEntryEntity
import com.habitiora.linkarium.data.local.usecase.toListTagEntity
import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.model.LinkTag
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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

    override fun getAll(): Flow<List<LinkSeed>> = linkSeedDataSource.getAll()
        .map { seeds -> seeds.map { it.toFullDomain() } }
    
    override fun getById(id: Long): Flow<LinkSeed?> = linkSeedDataSource.getById(id)
        .map { it?.toFullDomain() }

    override fun getSeedsByGarden(gardenId: Long): Flow<PagingData<LinkSeed>> =
        Pager(
            config = PAGING_CONFIG,
            pagingSourceFactory = { linkSeedDataSource.getSeedsByGarden(gardenId) }
        ).flow.map { paging ->
            paging.map { entity -> entity.toFullDomain() }
        }

    // ---------------------------------------------------------
    // WRITE - Con sincronización de caché
    // ---------------------------------------------------------

    override suspend fun insert(linkSeed: LinkSeedImpl): Result<Long> =
        db.withTransaction {
            runCatching {
                validateSeedData(linkSeed)
                require(validateGarden(linkSeed.gardenId)) { "Garden not found" }

                // Insert seed
                val id = linkSeedDataSource.insert(linkSeed)
                require(validateSeed(id)) { "Seed not found after insert" }

                // Insert related entities
                val links = linkSeed.links.toListEntryEntity(id)
                val linksIds = linkEntryDataSource.insertAll(links)
                require(linksIds.size == links.size) { "Not all links were inserted" }

                val tagErrors = handleTags(linkSeed.tags, id)
                require(tagErrors.isEmpty()) { "Error handling tags: ${tagErrors.size} failed" }
                id
            }.onSuccess { id ->
                Timber.d("✅ Seed inserted and cached: $id")
            }.onFailure { e ->
                Timber.e(e, "❌ Error inserting seed")
            }
        }

    override suspend fun update(linkSeed: LinkSeed): Result<Unit> =
        db.withTransaction {
            runCatching {
                require(linkSeed.id > 0) { "Invalid seed ID" }
                validateSeedData(linkSeed)
                require(validateGarden(linkSeed.gardenId)) { "Garden not found" }
                require(validateSeed(linkSeed.id)) { "Seed not found" }

                Timber.d("🔄 Updating seed: ${linkSeed.id}")

                // Update seed
                linkSeedDataSource.update(linkSeed)

                // Update related entities
                val linkErrors = handleLinks(linkSeed.links, linkSeed.id)
                require(linkErrors.isEmpty()) { "Error handling links: ${linkErrors.size} failed" }

                val tagErrors = handleTags(linkSeed.tags, linkSeed.id)
                require(tagErrors.isEmpty()) { "Error handling tags: ${tagErrors.size} failed" }
            }.onSuccess {
                Timber.d("✅ Seed updated and cached: ${linkSeed.id}")
            }.onFailure { e ->
                Timber.e(e, "❌ Error updating seed: ${linkSeed.id}")
            }
        }

    // ---------------------------------------------------------
    // DELETE - Con limpieza de caché
    // ---------------------------------------------------------

    override suspend fun delete(linkSeed: LinkSeed): Result<Unit> =
        db.withTransaction {
            runCatching {
                require(linkSeed.id > 0) { "Invalid seed ID" }
                linkSeedDataSource.delete(linkSeed)
            }.onSuccess {
                Timber.d("✅ Seed deleted and removed from cache: ${linkSeed.id}")
            }.onFailure { e ->
                Timber.e(e, "❌ Error deleting seed: ${linkSeed.id}")
            }
        }

    override suspend fun deleteById(id: Long): Result<Unit> =
        db.withTransaction {
            runCatching {
                require(id > 0) { "Invalid seed ID" }
                linkSeedDataSource.deleteById(id)
            }.onSuccess {
                Timber.d("✅ Seed deleted and removed from cache: $id")
            }.onFailure { e ->
                Timber.e(e, "❌ Error deleting seed: $id")
            }
        }

    override suspend fun deleteAll(): Result<Unit> =
        db.withTransaction {
            runCatching {
                linkSeedDataSource.deleteAll()
            }.onSuccess {
                Timber.d("✅ All seeds deleted and cache cleared")
            }.onFailure { e ->
                Timber.e(e, "❌ Error deleting all seeds")
            }
        }

    // ---------------------------------------------------------
    // Métodos auxiliares privados
    // ---------------------------------------------------------

    private fun validateSeedData(linkSeed: LinkSeed) {
        require(linkSeed.name.isNotBlank()) { "Seed name cannot be empty" }
        // Agrega más validaciones según necesites
    }

    private suspend fun validateGarden(gardenId: Long): Boolean {
        // Si no está en caché, consulta BD
        val garden = linkGardenDataSource.getById(gardenId).first()
        Timber.d("Garden validation: ${garden != null} for ID: $gardenId")
        return garden != null
    }

    private suspend fun validateSeed(seedId: Long): Boolean {
        val seed = linkSeedDataSource.getById(seedId).first()
        Timber.d("Seed validation: ${seed != null} for ID: $seedId")
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
                Timber.e(e, "❌ Error handling tag: $tag")
                errorTags.add(tag)
            }
        }
        if (errorTags.isNotEmpty()) {
            Timber.w("⚠️ Failed to handle ${errorTags.size} tags")
        }
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
                Timber.e(e, "❌ Error handling link: $link")
                errorLinks.add(link)
            }
        }
        if (errorLinks.isNotEmpty()) {
            Timber.w("⚠️ Failed to handle ${errorLinks.size} links")
        }
        return errorLinks
    }

    /**
     * Extension function para convertir una entidad de BD a dominio con todas sus relaciones
     */
    private suspend fun LinkSeedEntity.toFullDomain(): LinkSeed =
        db.withTransaction {
            val links = linkEntryDataSource.getLinksBySeed(id).first()
            val tags = linkTagDataSource.getTagsBySeed(id).first()
            toDomain(links, tags)
        }
}