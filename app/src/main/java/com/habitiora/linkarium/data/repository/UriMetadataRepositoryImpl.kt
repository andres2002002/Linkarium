package com.habitiora.linkarium.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.habitiora.linkarium.core.UriUtils.toHttpsUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import timber.log.Timber
import javax.inject.Inject

class UriMetadataRepositoryImpl @Inject constructor() : UriMetadataRepository {
    override suspend fun extractThumbnailFromUrl(uri: Uri?): Uri? = withContext(Dispatchers.IO) {
        val httpUri = uri?.toHttpsUri()
        try {
            // Conecta a la URL y extrae el documento HTML
            val document = Jsoup.connect(httpUri.toString())
                .userAgent("Mozilla/5.0 (Android 14; Mobile; rv:109.0) Gecko/113.0 Firefox/113.0")
                .timeout(5000) // 5s timeout
                .get()

            // Busca la etiqueta meta de Open Graph para la imagen
            val metaOgImage = document.select("meta[property=og:image]").attr("content").takeIf { it.isNotEmpty() }
                ?: document.select("meta[name=twitter:image]").attr("content").takeIf { it.isNotEmpty() }
                ?: document.select("link[rel=apple-touch-icon]").attr("href").takeIf { it.isNotEmpty() }
                ?: document.select("link[rel=icon]").attr("href").takeIf { it.isNotEmpty() }

            // Resolver URLs relativas para la imagen si es necesario
            val finalImageUrl = metaOgImage?.let {
                if (it.startsWith("http")) it else resolveRelativeUrl(httpUri.toString(), it)
            }
            finalImageUrl?.toUri()
        } catch (e: Exception) {
            Timber.e(e, "Error extrayendo thumbnail de: $httpUri")
            null
        }
    }

    private fun resolveRelativeUrl(baseUrl: String, relativePath: String): String {
        return try {
            val base = java.net.URI(baseUrl)
            base.resolve(relativePath).toString()
        } catch (e: Exception) {
            relativePath
        }
    }
}