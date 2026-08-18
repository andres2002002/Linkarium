package com.habitiora.linkarium.core

import android.net.Uri
import android.util.Patterns
import android.webkit.URLUtil
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.habitiora.linkarium.core.UriUtils.toHttpsUri
import com.habitiora.linkarium.core.UriUtils.toUriSafe

interface UriValidator {
    fun isNetworkUrl(uri: Uri?): Boolean
    fun isNetworkUrl(url: String?): Boolean
    fun isLocalResource(uri: Uri?): Boolean
    fun isValidResource(uri: Uri?): Boolean
    fun isValidResource(url: String?): Boolean
}

@Singleton
class UriValidatorImpl @Inject constructor() : UriValidator {

    private companion object {
        // Esquemas de red estrictos
        val NETWORK_SCHEMES = setOf("http", "https", "mailto")
        // Esquemas locales soportados
        val LOCAL_SCHEMES = setOf("content", "file", "asset")
    }

    override fun isNetworkUrl(uri: Uri?): Boolean {
        if (uri == null) return false
        Timber.d("Uri passed: $uri")
        val resolvedUri = uri.toHttpsUri()
        Timber.d("Uri resolved: $resolvedUri")
        val scheme = resolvedUri.scheme?.lowercase() ?: return false
        Timber.d("Uri scheme: $scheme")

        // Fail-fast: Si el esquema no es http/s, no gastamos tiempo en Regex
        if (scheme !in NETWORK_SCHEMES) return false
        Timber.d("Uri scheme is valid")

        // Validación estricta usando herramientas nativas de Android
        val uriString = resolvedUri.toString()
        val isNetworkUrl = URLUtil.isNetworkUrl(uriString)
        val isPatternMatch = Patterns.WEB_URL.matcher(uriString).matches()
        Timber.d("Uri is network URL: $isNetworkUrl")
        Timber.d("Uri matches pattern: $isPatternMatch")

        return isNetworkUrl && isPatternMatch
    }

    override fun isNetworkUrl(url: String?): Boolean {
        return url?.toUriSafe()?.let { isNetworkUrl(it) } ?: false
    }

    override fun isLocalResource(uri: Uri?): Boolean {
        if (uri == null) return false
        val scheme = uri.scheme?.lowercase() ?: return false
        return scheme in LOCAL_SCHEMES
    }

    /**
     * Valida si la URI es un recurso válido para ser cargado por la app
     * (cubre tanto Web como Content/File providers).
     * Reemplaza tu lógica original de 'isLikelyValidWebUri' con un nombre más apropiado.
     */
    override fun isValidResource(uri: Uri?): Boolean {
        return isNetworkUrl(uri) || isLocalResource(uri)
    }

    override fun isValidResource(url: String?): Boolean {
        return url?.toUriSafe()?.let { isValidResource(it) } ?: false
    }
}