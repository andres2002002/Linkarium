package com.habitiora.linkarium.domain.model

interface LinkGardenWithSeeds {
    val garden: LinkGarden
    val seeds: List<LinkSeed>
}