package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.dao.LinkGardenReadDao
import com.habitiora.linkarium.data.local.room.dao.LinkSeedReadDao
import com.habitiora.linkarium.data.local.room.relations.LinkGardenAggregate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExportDataSource @Inject constructor(
    private val linkGardenReadDao: LinkGardenReadDao,
    private val linkSeedReadDao: LinkSeedReadDao
) {

    suspend fun countGardens(): Int = linkGardenReadDao.count()

    suspend fun getGardensWithSeeds(ids: List<Long>): List<LinkGardenAggregate> =
            linkGardenReadDao.getGardensForExport(ids)

    suspend fun getAllGardensWithSeeds(): List<LinkGardenAggregate> =
        linkGardenReadDao.getAllGardensForExport()

    fun getAllGardensWithSeedsFlow(): Flow<List<LinkGardenAggregate>> =
        linkGardenReadDao.getAllGardensForExportFlow()

    fun getGardensWithSeedsFlow(ids: List<Long>): Flow<List<LinkGardenAggregate>> =
        linkGardenReadDao.getGardensForExportFlow(ids)
}