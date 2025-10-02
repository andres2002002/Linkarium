package com.habitiora.linkarium.ui.utils.uirHelper

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

class UriHelper(private val context: Context) {
    fun open(url: String) {
        open(url.toUri())
    }

    fun open(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Manejo de error: navegador no disponible
            Toast.makeText(context, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show()
        }
    }
}