package com.habitiora.linkarium.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.habitiora.linkarium.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    private object PreferencesKeys {
        val USE_BIOMETRIC_LOCK = booleanPreferencesKey("use_biometric_lock")
        val ALLOW_NOTIFICATIONS = booleanPreferencesKey("allow_notifications")
        val AUTO_CLEAR_CACHE = booleanPreferencesKey("auto_clear_cache")
        val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
    }

    override val userPreferences: Flow<UserPreferences>
        get() = dataStore.data.map { preferences ->
            UserPreferences(
                useBiometricLock = preferences[PreferencesKeys.USE_BIOMETRIC_LOCK] ?: false,
                allowNotifications = preferences[PreferencesKeys.ALLOW_NOTIFICATIONS] ?: true,
                autoClearCache = preferences[PreferencesKeys.AUTO_CLEAR_CACHE] ?: false,
                lastSyncTimestamp = preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] ?: 0L
            )
        }


    override suspend fun updateBiometricLock(enabled: Boolean): Result<Unit> =
        runCatching {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.USE_BIOMETRIC_LOCK] = enabled
            }
        }

    override suspend fun updateNotifications(enabled: Boolean): Result<Unit> =
        runCatching {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.ALLOW_NOTIFICATIONS] = enabled
            }
        }
}