package com.habitiora.linkarium.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserPreferences(
    val useBiometricLock: Boolean = false,
    val allowNotifications: Boolean = true,
    val autoClearCache: Boolean = false,
    val lastSyncTimestamp: Long = 0L
)