package com.habitiora.linkarium.domain.usecase

import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed

data class LinkGardenWithSeedsImpl(
    override val garden: LinkGarden,
    override val seeds: List<LinkSeed>
): LinkGardenWithSeeds