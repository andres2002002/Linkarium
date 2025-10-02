package com.habitiora.linkarium.ui.utils.clipBoardHelper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberClipboardHelper(): ClipboardHelper {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    return remember(clipboard, context) { ClipboardHelper(clipboard, context) }
}