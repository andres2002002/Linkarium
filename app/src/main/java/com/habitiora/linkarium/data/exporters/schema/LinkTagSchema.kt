package com.habitiora.linkarium.data.exporters.schema

import com.habitiora.linkarium.core.exporters.ExportNode
import com.habitiora.linkarium.core.exporters.ExportNode.ExportField
import com.habitiora.linkarium.core.exporters.ExportSchema
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.domain.model.LinkTag

object LinkTagSchema {
    val schema = ExportSchema(
        listOf(
            ExportField("id", LinkTagEntity::id),
            ExportField("seedId", LinkTagEntity::seedId),
            ExportField("tag", LinkTagEntity::tag)
        )
    )

    val domainSchema = ExportSchema(
        listOf(
            ExportField("id", LinkTag::id),
            ExportField("seedId", LinkTag::seedId),
            ExportField("tag", LinkTag::tag)
        )
    )
}