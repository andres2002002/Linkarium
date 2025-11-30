package com.habitiora.linkarium.data.repository

import androidx.room.withTransaction
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.domain.model.LinkGarden
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

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
                val maxOrder = gardenDataSource.getMaxOrder()
                val id = gardenDataSource.insert(linkGarden.update(order = maxOrder + 1))
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

    override suspend fun update(linkGardens: List<LinkGarden>): Result<Unit> =
        db.withTransaction {
            runCatching {
                gardenDataSource.update(linkGardens)
            }.onSuccess {
                Timber.d("Updated ${linkGardens.size} gardens")
            }.onFailure { e ->
                Timber.e(e, "Error updating gardens")
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