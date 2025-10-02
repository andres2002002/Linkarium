package com.habitiora.linkarium.ui.utils.clipBoardHelper

import android.content.ClipData
import android.content.Context
import android.net.Uri
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ClipboardHelper(
    private val clipboard: Clipboard,
    private val context: Context
) {
    suspend fun copyAsPlainText(label: String, text: String) {
        val clipData = ClipData.newPlainText(label, text)
        clipboard.setClipEntry(clipData.toClipEntry())
    }

    suspend fun copyAsHtmlText(label: String, text: String) {
        val clipData = ClipData.newHtmlText(label, text, "text/html")
        clipboard.setClipEntry(clipData.toClipEntry())
    }

    suspend fun copyAsUri(label: String, uri: String) {
        copyAsUri(label, uri.toUri())
    }

    suspend fun copyAsUri(label: String, uri: Uri) {
        val clipData = ClipData.newPlainText(label, uri.toString())
        clipboard.setClipEntry(clipData.toClipEntry())
    }
}