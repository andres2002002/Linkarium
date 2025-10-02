package com.habitiora.linkarium.domain.model

import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedComplete

interface LinkGardenWithSeeds {
    val garden: LinkGarden
    val seeds: List<LinkSeed>
}