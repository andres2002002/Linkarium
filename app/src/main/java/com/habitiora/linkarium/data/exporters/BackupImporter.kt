package com.habitiora.linkarium.data.exporters

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.habitiora.linkarium.core.exporters.ImportStatus
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.dao.LinkEntryEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkGardenEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkTagEntityDao
import com.habitiora.linkarium.data.local.room.entity.LinkEntryEntity
import com.habitiora.linkarium.data.local.room.entity.LinkGardenEntity
import com.habitiora.linkarium.data.local.room.entity.LinkSeedEntity
import com.habitiora.linkarium.data.local.room.entity.LinkTagEntity
import com.habitiora.linkarium.data.local.room.relations.LinkGardenAggregate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.zip.GZIPInputStream
import javax.inject.Inject

@Serializable
data class FullBackupContainer(
    val meta: AppBackupMetadata,
    val data: List<LinkGardenAggregate>
)

class BackupImporter @Inject constructor(
    private val gardenDao: LinkGardenEntityDao,
    private val seedDao: LinkSeedEntityDao,
    private val entryDao: LinkEntryEntityDao,
    private val tagsDao: LinkTagEntityDao,
    private val db: AppDatabase,
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun execute(uri: Uri): Flow<ImportStatus> = flow {
        emit(ImportStatus.InProgress)

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open output stream for URI: $uri")
            // 1. Descomprimir y leer todo el texto (asumiendo tamaño razonable < 100MB)
            // Si el archivo es GIGANTE, aquí se requiere una estrategia de SAX Parser o JsonReader.
            val jsonString = withContext(Dispatchers.IO) {
                GZIPInputStream(inputStream).bufferedReader(Charsets.UTF_8).use { it.readText() }
            }

            // 2. Deserializar estructura
            val container = json.decodeFromString<FullBackupContainer>(jsonString)
            val gardens = container.data

            // 3. Insertar en DB (Transaccional)
            // Recomendación: Borrar todo antes (Restore) o Merge (Import)
            // Aquí hacemos un Restore (Borra y escribe)

            val allGardens = mutableListOf<LinkGardenEntity>()
            val allSeeds = mutableListOf<LinkSeedEntity>()
            val allTags = mutableListOf<LinkTagEntity>()
            val allEntries = mutableListOf<LinkEntryEntity>()
            gardens.forEach { gardenAgg ->
                allGardens.add(gardenAgg.garden)

                gardenAgg.seeds.forEach { seedAgg ->
                    allSeeds.add(seedAgg.seed)
                    allTags.addAll(seedAgg.tags)
                    allEntries.addAll(seedAgg.links)
                }
            }
            // Usamos una transaction para que se maneje en bloque
            db.withTransaction {
                seedDao.upsertAll(allSeeds)
                gardenDao.upsertAll(allGardens)
                entryDao.upsertAll(allEntries)
                tagsDao.upsertAll(allTags)
            }
            emit(ImportStatus.Success(uri))

        } catch (e: Exception) {
            emit(ImportStatus.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}