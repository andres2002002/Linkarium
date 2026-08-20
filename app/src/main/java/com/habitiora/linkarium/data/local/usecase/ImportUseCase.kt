package com.habitiora.linkarium.data.local.usecase

import android.net.Uri
import com.habitiora.linkarium.core.exporters.ImportStatus
import com.habitiora.linkarium.data.exporters.BackupImporter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImportUseCase @Inject constructor(
    private val importer: BackupImporter
) {
    operator fun invoke(uri: Uri): Flow<ImportStatus> {
        return importer.execute(uri)
    }
}