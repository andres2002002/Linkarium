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
    // Obtener todos los jardines
    override fun getAll(): Flow<List<LinkGarden>> =
        gardenDataSource.getAll()

    override fun getById(id: Long): Flow<LinkGarden?> =
        gardenDataSource.getById(id)

    /**
     * Inserta un nuevo jardín con validaciones
     */
    override suspend fun insert(linkGarden: LinkGarden): Result<Long> =
        runCatching {
            require(linkGarden.name.isNotBlank()) { "El nombre no puede estar vacío" }
            require(linkGarden.id <= 0) { "El ID debe ser 0 o negativo para inserción" }
            db.withTransaction {
                val maxOrder = gardenDataSource.getMaxOrder()
                gardenDataSource.insert(linkGarden.update(order = maxOrder + 1))
            }
        }

    /**
     * Actualiza un jardín existente
     * Invalida caché relacionado para mantener consistencia
     */
    override suspend fun update(linkGarden: LinkGarden): Result<Unit> =
        runCatching {
            require(linkGarden.id > 0) { "El ID debe ser válido para actualización" }
            require(linkGarden.name.isNotBlank()) { "El nombre no puede estar vacío" }
            gardenDataSource.update(linkGarden)
        }

    override suspend fun update(linkGardens: List<LinkGarden>): Result<Unit> =
        runCatching { gardenDataSource.update(linkGardens) }


    /**
     * Elimina un jardín y todas sus seeds asociadas
     * Limpieza atómica de caché y BD
     */
    override suspend fun delete(linkGarden: LinkGarden): Result<Unit> =
        runCatching {
            require(linkGarden.id > 0) { "El ID debe ser válido" }
            gardenDataSource.delete(linkGarden)
        }


    /**
     * Elimina un jardín por ID con limpieza completa
     */
    override suspend fun deleteById(id: Long): Result<Unit> =
        runCatching { gardenDataSource.deleteById(id) }

    /**
     * Elimina todos los jardines y limpia todos los datos del caché
     */
    override suspend fun deleteAll(): Result<Unit> =
        runCatching { gardenDataSource.deleteAll() }
}