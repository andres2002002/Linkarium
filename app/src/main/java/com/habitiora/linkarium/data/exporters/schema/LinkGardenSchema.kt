package com.habitiora.linkarium.data.exporters.schema

import com.habitiora.linkarium.core.exporters.ExportSchema
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.core.exporters.ExportNode.ExportField


object LinkGardenSchema {
    val schema = ExportSchema(
        listOf(
            ExportField("id", LinkGarden::id),
            ExportField("name", LinkGarden::name),
            ExportField("description", LinkGarden::description),
            ExportField("order", LinkGarden::order)
        )
    )
}
