package com.habitiora.linkarium.ui.utils.uirHelper

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

class UriHelper(private val context: Context) {
    private fun validateUrl(uri: Uri): Boolean {
        val schemeValid = uri.scheme in listOf("http", "https")
        val hostValid = uri.host != null
        return schemeValid && hostValid
    }

    private fun repairUri(uri: Uri): Uri{
        // Si la URI ya tiene un esquema válido, no la modificamos.
        if (validateUrl(uri)) return uri

        val uriString = uri.toString()
        // Si no tiene esquema, pero parece una URL (contiene un punto), intentamos agregar https://
        return if (uriString.contains('.') && !uriString.contains(' ')) {
            "https://".plus(uriString).toUri()
        } else {
            // Si no, asumimos que es una búsqueda.
            "https://www.google.com/search?q=${uriString.replace(" ", "+")}".toUri()
        }
    }

    fun open(uri: Uri) {
        try {
            val repairedUri = repairUri(uri)
            val intent = Intent(Intent.ACTION_VIEW, repairedUri)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Manejo de error: navegador no disponible
            Toast.makeText(context, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
        }
    }
}