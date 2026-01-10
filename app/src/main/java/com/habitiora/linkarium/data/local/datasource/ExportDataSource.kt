package com.habitiora.linkarium.data.local.datasource

import androidx.room.withTransaction
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.usecase.toDomain
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportDataSource @Inject constructor(
    private val db: AppDatabase,
    private val gardenDataSource: LinkGardenDataSource,
    private val seedDataSource: LinkSeedDataSource,
    private val entryDataSource: LinkEntryDataSource,
    private val tagDataSource: LinkTagDataSource
) {

    suspend fun getGardensWithSeeds(ids: List<Long>): Map<LinkGarden, List<LinkSeed>> =
        db.withTransaction {
            val gardens = gardenDataSource.getForList(ids)
            gardens.associateWith { garden ->
                getSeedsOf(garden.id)
            }
        }

    suspend fun getAllGardensWithSeeds(): Map<LinkGarden, List<LinkSeed>> =
        db.withTransaction {
            val gardens = gardenDataSource.getAllForExport()
            gardens.associateWith { garden ->
                getSeedsOf(garden.id)
            }
        }

    suspend fun getSeedsOf(gardenId: Long): List<LinkSeed> =
        seedDataSource.getSeedsForExport(gardenId)
            .map { getLinkSeedDomain(it) }

    suspend fun getLinkSeedDomain(seed: LinkSeedEntity): LinkSeed {
        val entries = entryDataSource.getLinksBySeed(seed.id).first()
        val tags = tagDataSource.getTagsBySeed(seed.id).first()
        return seed.toDomain(entries, tags)
    }
}