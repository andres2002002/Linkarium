package com.habitiora.linkarium.ui.screens.plantSeed

import android.net.Uri
import com.habitiora.linkarium.core.UriValidator
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.data.repository.UriMetadataRepository
import com.habitiora.linkarium.domain.usecase.LinkSeedImpl
import com.habitiora.linkarium.ui.utils.pubsAndSubs.GardenSelectionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PlantSeedUseCases @Inject constructor(
    private val seedRepository: LinkSeedRepository,
    private val gardenRepository: LinkGardenRepository,
    private val uriMetadataRepository: UriMetadataRepository,
    private val uriValidator: UriValidator,
    private val gardenSelectionManager: GardenSelectionManager
) {
    fun getAllGardens() = gardenRepository.getAll()
    fun getSelectedGardenIndex() = gardenSelectionManager.selectedGardenIndex
    fun selectGarden(index: Int) = gardenSelectionManager.selectGarden(index)

    suspend fun getSeedById(id: Long) = seedRepository.getById(id).first()
    suspend fun extractThumbnail(uri: Uri): Uri? = uriMetadataRepository.extractThumbnailFromUrl(uri)
    fun isValidResource(uri: Uri) = uriValidator.isValidResource(uri)
    fun isValidResourceString(url: String) = uriValidator.isValidResource(url)

    suspend fun saveSeed(seed: LinkSeedImpl, isEditMode: Boolean): Result<Any> {
        return if (isEditMode) seedRepository.update(seed) else seedRepository.insert(seed)
    }
}