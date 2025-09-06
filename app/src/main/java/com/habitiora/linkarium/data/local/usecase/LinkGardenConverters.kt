package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.domain.model.LinkGarden

fun LinkGarden.toEntity(): LinkGardenEntity = LinkGardenEntity(
    id = this.id,
    name = this.name,
    description = this.description
)

fun LinkGardenEntity.toDomain(): LinkGarden = this