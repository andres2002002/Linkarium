package com.habitiora.linkarium.data.exporters.schema

import com.habitiora.linkarium.core.exporters.ExportSchema
import com.habitiora.linkarium.data.local.room.relations.LinkGardenAggregate
import com.habitiora.linkarium.core.exporters.ExportNode.ExportNestedEntity
import com.habitiora.linkarium.core.exporters.ExportNode.ExportNestedList

object LinkGardenAggregateSchema {
    val schema = ExportSchema(
        listOf(
            ExportNestedEntity("garden", LinkGardenAggregate::garden, LinkGardenSchema.schema),
            ExportNestedList("seeds", LinkGardenAggregate::seeds, LinkSeedAggregateSchema.schema)
        )
    )
}