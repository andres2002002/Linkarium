package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.data.local.room.entity.GardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds

fun LinkGardenWithSeeds.toEntity(): GardenWithSeeds =
    GardenWithSeeds(
        garden = this.garden.toEntity(),
        seeds = this.seeds.map { it.toEntity() }
    )
fun GardenWithSeeds.toDomain(): LinkGardenWithSeeds = this