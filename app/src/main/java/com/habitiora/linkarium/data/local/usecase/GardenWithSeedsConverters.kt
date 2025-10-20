package com.habitiora.linkarium.data.local.usecase

import com.habitiora.linkarium.domain.usecase.LinkGardenWithSeedsImpl
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds

fun LinkGardenWithSeeds.toEntity(): LinkGardenWithSeedsImpl =
    LinkGardenWithSeedsImpl(
        garden = this.garden.toEntity(),
        seeds = this.seeds
    )
fun LinkGardenWithSeedsImpl.toDomain(): LinkGardenWithSeeds = this