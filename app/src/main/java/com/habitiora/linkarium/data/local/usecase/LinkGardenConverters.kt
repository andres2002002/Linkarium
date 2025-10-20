package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.usecase.LinkGardenWithSeedsImpl

fun LinkGarden.toEntity(): LinkGardenEntity = LinkGardenEntity(
    id = this.id,
    name = this.name,
    description = this.description
)

fun LinkGardenEntity.toDomain(): LinkGarden = this

fun LinkGarden.toGardenWithSeeds(seeds: List<LinkSeed>): LinkGardenWithSeeds =
    LinkGardenWithSeedsImpl(
        garden = this,
        seeds = seeds
    )
