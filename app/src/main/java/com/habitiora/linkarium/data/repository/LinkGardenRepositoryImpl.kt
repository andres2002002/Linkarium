package com.habitiora.linkarium.data.repository

import androidx.room.withTransaction
import com.habitiora.linkarium.core.ProcessStatus
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSource
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.entity.LinkSeedComplete
import com.habitiora.linkarium.data.local.usecase.toComplete
import com.habitiora.linkarium.data.local.usecase.toDomain
import com.habitiora.linkarium.data.local.usecase.toGardenWithSeeds
import com.habitiora.linkarium.data.local.usecase.toListDomain
import com.habitiora.linkarium.domain.model.LinkGarden
import com.habitiora.linkarium.domain.model.LinkGardenWithSeeds
import com.habitiora.linkarium.domain.model.LinkSeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map
import kotlin.compareTo

@Singleton
class LinkGardenRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val gardenDataSource: LinkGardenDataSource
): LinkGardenRepository {

    // ---------------------------------------------------------
    // 📖 READ Operations - Cache-First Strategy
    // ---------------------------------------------------------

    // Obtener todos los jardines con soporte de caché
    override fun getAll(): Flow<List<LinkGarden>> =
        gardenDataSource.getAll()

    override fun getById(id: Long): Flow<LinkGarden?> =
        gardenDataSource.getById(id)

    /**
     * Inserta un nuevo jardín con validaciones
     */
    override suspend fun insert(linkGarden: LinkGarden): Result<Long> =
        db.withTransaction {
            runCatching {
                require(linkGarden.name.isNotBlank()) { "El nombre no puede estar vacío" }
                require(linkGarden.id <= 0) { "El ID debe ser 0 o negativo para inserción" }
                val id = gardenDataSource.insert(linkGarden)
                id
            }.onSuccess { id ->
                Timber.d("Inserted garden with id: $id")
            }.onFailure { e ->
                Timber.e(e, "Error inserting garden")
            }
        }


    /**
     * Actualiza un jardín existente
     * Invalida caché relacionado para mantener consistencia
     */
    override suspend fun update(linkGarden: LinkGarden): Result<Unit> =
        db.withTransaction {
            runCatching {
                require(linkGarden.id > 0) { "El ID debe ser válido para actualización" }
                require(linkGarden.name.isNotBlank()) { "El nombre no puede estar vacío" }
                gardenDataSource.update(linkGarden)
            }.onSuccess {
                Timber.d("Updated garden with id: ${linkGarden.id}")
            }.onFailure { e ->
                Timber.e(e, "Error updating garden")
            }
        }


    /**
     * Elimina un jardín y todas sus seeds asociadas
     * Limpieza atómica de caché y BD
     */
    override suspend fun delete(linkGarden: LinkGarden): Result<Unit> =
        db.withTransaction {
            runCatching {
                require(linkGarden.id > 0) { "El ID debe ser válido" }
                deleteById(linkGarden.id).getOrThrow()
            }.onSuccess {
                Timber.d("Deleted garden with id: ${linkGarden.id}")
            }.onFailure { e ->
                Timber.e(e, "Error deleting garden")
            }
        }


    /**
     * Elimina un jardín por ID con limpieza completa
     */
    override suspend fun deleteById(id: Long): Result<Unit> =
        db.withTransaction {
            runCatching {
                require(id > 0) { "El ID debe ser válido" }
                gardenDataSource.deleteById(id)
            }.onSuccess {
                Timber.d("Deleted garden with id: $id")
            }.onFailure { e ->
                Timber.e(e, "Error deleting garden")
            }
        }


    /**
     * Elimina todos los jardines y limpia todos los datos del caché
     */
    override suspend fun deleteAll(): Result<Unit> =
        db.withTransaction {
            runCatching {
                gardenDataSource.deleteAll()
            }.onSuccess {
                Timber.d("Deleted all gardens")
            }.onFailure { e ->
                Timber.e(e, "Error deleting all gardens")
            }
        }
}