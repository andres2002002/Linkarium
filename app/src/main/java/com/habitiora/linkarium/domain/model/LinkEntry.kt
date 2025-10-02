package com.habitiora.linkarium.domain.model

import android.net.Uri

interface LinkEntry{
    val id: Long
    val seedId: Long
    val uri: Uri
    val label: String?
    val note: String?
}
