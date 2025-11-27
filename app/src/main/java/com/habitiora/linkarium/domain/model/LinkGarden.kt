package com.habitiora.linkarium.domain.model

interface LinkGarden {
    val id: Long
    val name: String
    val description: String
    val order: Int

    fun update(
        id: Long = this.id,
        name: String = this.name,
        description: String = this.description
    ): LinkGarden
}