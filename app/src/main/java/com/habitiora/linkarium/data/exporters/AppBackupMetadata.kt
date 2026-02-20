package com.habitiora.linkarium.data.exporters

import kotlinx.serialization.Serializable

@Serializable
data class AppBackupMetadata(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val device: String = android.os.Build.MODEL
)