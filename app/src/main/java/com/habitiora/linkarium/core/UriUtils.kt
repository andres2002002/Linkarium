package com.habitiora.linkarium.core

import android.net.Uri
import androidx.core.net.toUri

object UriUtils{
    fun String.toUriSafe(): Uri? {
        if (this.isBlank()) return null
        return runCatching { this.trim().toUri() }.getOrNull()
    }
    /**
     * Normaliza y fuerza el esquema HTTPS en URIs web.
     * Maneja URIs sin esquema, esquemas HTTP legados y falsos positivos del parser de Android.
     */
    fun Uri.toHttpsUri(): Uri {
        val currentScheme = this.scheme?.lowercase()
        val uriString = this.toString()

        return when {
            currentScheme == "https" -> this
            currentScheme == "http" -> this.buildUpon().scheme("https").build()
            currentScheme == null -> {
                // Maneja casos como "www.domain.com" o "//domain.com"
                val cleanedPath = uriString.removePrefix("//")
                "https://$cleanedPath".toUri()
            }
            currentScheme.contains(".") -> {
                // Workaround para el parser de Android:
                // Uri.parse("domain.com:8080") detecta "domain.com" como scheme.
                // Los schemes RFC estándar no usan "." habitualmente.
                val cleanedPath = uriString.removePrefix("//")
                "https://$cleanedPath".toUri()
            }
            else -> {
                // Retorna la URI intacta para deeplinks o esquemas estándar no web (mailto:, tel:, intent:)
                this
            }
        }
    }
}
