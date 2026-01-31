package com.habitiora.linkarium.ui.scaffold

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.core.AuthState
import com.habitiora.linkarium.core.BiometricAuthenticator
import com.habitiora.linkarium.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val biometricAuthenticator: BiometricAuthenticator,
    private val preferences: PreferencesRepository
) : ViewModel() {

    private val _isLocked = MutableStateFlow(true)

    // Transformamos el flujo para que empiece en Loading
    val authState: StateFlow<AuthState> = combine(
        preferences.userPreferences,
        _isLocked
    ) { prefs, locked ->
        if (prefs.useBiometricLock && locked) {
            AuthState.Locked
        } else {
            AuthState.Unlocked
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState.Loading // <--- CLAVE: Empieza aquí
    )

    // El estado final combina: ¿El usuario quiere bloqueo? Y ¿Está bloqueada ahora?
    val lockState = combine(preferences.userPreferences, _isLocked) { prefs, locked ->
        if (prefs.useBiometricLock) locked else false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun lock() {
        _isLocked.value = true
    }

    fun unlock() {
        _isLocked.value = false
    }

    fun checkBiometrics(activity: FragmentActivity) {
        if (biometricAuthenticator.canAuthenticate()) {
            biometricAuthenticator.prompt(
                activity = activity,
                onSuccess = { unlock() },
                onError = { lock() }
            )
        } else {
            unlock() // Fallback si no hay hardware
        }
    }
}