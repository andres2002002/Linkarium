package com.habitiora.linkarium.ui.utils.uirHelper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberUriHelper(): UriHelper {
    val context = LocalContext.current
    return remember(context) { UriHelper(context) }
}