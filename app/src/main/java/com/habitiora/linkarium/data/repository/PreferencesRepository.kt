package com.habitiora.linkarium.data.repository

import com.habitiora.linkarium.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateBiometricLock(enabled: Boolean): Result<Unit>
    suspend fun updateNotifications(enabled: Boolean): Result<Unit>

}