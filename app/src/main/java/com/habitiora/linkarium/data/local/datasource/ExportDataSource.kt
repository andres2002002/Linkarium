package com.habitiora.linkarium.data.local.datasource

import com.habitiora.linkarium.data.local.room.dao.LinkGardenReadDao
import com.habitiora.linkarium.data.local.room.relations.LinkGardenAggregate
import javax.inject.Inject

class ExportDataSource @Inject constructor(
    private val linkGardenReadDao: LinkGardenReadDao
) {
    suspend fun getGardensWithSeeds(ids: List<Long>): List<LinkGardenAggregate> =
            linkGardenReadDao.getGardensForExport(ids)

    suspend fun getAllGardensWithSeeds(): List<LinkGardenAggregate> =
        linkGardenReadDao.getAllGardensForExport()
}