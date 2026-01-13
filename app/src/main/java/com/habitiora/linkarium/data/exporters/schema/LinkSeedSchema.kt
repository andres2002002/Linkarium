package com.habitiora.linkarium.data.exporters.schema

import com.habitiora.linkarium.core.exporters.ExportFormatters
import com.habitiora.linkarium.core.exporters.ExportSchema
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.core.exporters.ExportNode.ExportField
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity


object LinkSeedSchema {
    val schema = ExportSchema(
        listOf(
            ExportField("id", LinkSeedEntity::id),
            ExportField("name", LinkSeedEntity::name),
            ExportField("gardenId", LinkSeedEntity::gardenId),
            ExportField("order", LinkSeedEntity::order),
            ExportField(
                header = "isFavorite",
                property = LinkSeedEntity::isFavorite,
                formatter = ExportFormatters.boolean01
            ),
            ExportField("notes", LinkSeedEntity::notes),
            ExportField(
                header = "modifiedAt",
                property = LinkSeedEntity::modifiedAt,
                formatter = ExportFormatters.isoDateTime
            )
        )
    )

    private val domainSchema = ExportSchema(
        listOf(
            ExportField("id", LinkSeed::id),
            ExportField("name", LinkSeed::name),
            ExportField("gardenId", LinkSeed::gardenId),
            ExportField("order", LinkSeed::order),
            ExportField(
                header = "isFavorite",
                property = LinkSeed::isFavorite,
                formatter = ExportFormatters.boolean01
            ),
            ExportField("notes", LinkSeed::notes),
            ExportField(
                header = "modifiedAt",
                property = LinkSeed::modifiedAt,
                formatter = ExportFormatters.isoDateTime
            )
        )
    )
}
