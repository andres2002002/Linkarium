package com.habitiora.linkarium.data.exporters.schema

import com.habitiora.linkarium.core.exporters.ExportNode.ExportNestedEntity
import com.habitiora.linkarium.core.exporters.ExportNode.ExportNestedList
import com.habitiora.linkarium.core.exporters.ExportSchema
import com.habitiora.linkarium.data.local.room.relations.LinkSeedAggregate

object LinkSeedAggregateSchema {
    val schema = ExportSchema(
        listOf(
            ExportNestedEntity("seed", LinkSeedAggregate::seed, LinkSeedSchema.schema),
            ExportNestedList("tags", LinkSeedAggregate::tags, LinkTagSchema.schema),
            ExportNestedList("entries", LinkSeedAggregate::links, LinkEntrySchema.schema)
        )
    )
}