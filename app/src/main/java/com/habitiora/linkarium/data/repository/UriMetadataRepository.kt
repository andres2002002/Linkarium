package com.habitiora.linkarium.data.repository

import android.net.Uri

interface UriMetadataRepository {
    suspend fun extractThumbnailFromUrl(uri: Uri?): Uri?
}