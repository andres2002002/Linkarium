package com.habitiora.linkarium.data.exporters.schema

import com.habitiora.linkarium.core.exporters.ExportNode.ExportField
import com.habitiora.linkarium.core.exporters.ExportSchema
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.domain.model.LinkEntry

object LinkEntrySchema {

    val schema = ExportSchema(
        listOf(
            ExportField("id", LinkEntryEntity::id),
            ExportField("seedId", LinkEntryEntity::seedId),
            ExportField("order", LinkEntryEntity::order),
            ExportField("uri", LinkEntryEntity::uri),
            ExportField("label", LinkEntryEntity::label),
            ExportField("note", LinkEntryEntity::note)
        )
    )

    val domainSchema = ExportSchema(
        listOf(
            ExportField("id", LinkEntry::id),
            ExportField("seedId", LinkEntry::seedId),
            ExportField("order", LinkEntry::order),
            ExportField("uri", LinkEntry::uri),
            ExportField("label", LinkEntry::label),
            ExportField("note", LinkEntry::note)
        )
    )
}