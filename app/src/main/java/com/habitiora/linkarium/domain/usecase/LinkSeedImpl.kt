package com.habitiora.linkarium.domain.usecase

import com.habitiora.linkarium.domain.model.LinkEntry
import com.habitiora.linkarium.domain.model.LinkSeed
import com.habitiora.linkarium.domain.model.LinkTag
import java.time.LocalDateTime

/**
 * Implementación de la interfaz [LinkSeed]
 * @param id El ID de la semilla.
 * @param name El nombre de la semilla.
 * @param links La lista de enlaces asociados a la semilla.
 * @param gardenId El ID de la colección a la que pertenece la semilla.
 * @param isFavorite Indica si la semilla es favorita o no.
 * @param notes Notas adicionales sobre la semilla.
 * @param tags Etiquetas asociadas a la semilla.
 * @param modifiedAt La fecha y hora de la última modificación de la semilla.
 */
data class LinkSeedImpl(
    override val id: Long = 0,
    override val name: String = "",
    override val links: List<LinkEntry> = emptyList(),
    override val gardenId: Long = 0,
    override val isFavorite: Boolean = false,
    override val notes: String? = null,
    override val tags: List<LinkTag> = emptyList(),
    override val modifiedAt: LocalDateTime = LocalDateTime.now()
): LinkSeed
