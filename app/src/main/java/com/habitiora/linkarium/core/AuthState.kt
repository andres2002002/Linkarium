package com.habitiora.linkarium.core

sealed interface AuthState {
    data object Loading : AuthState   // Estamos leyendo disco
    data object Locked : AuthState    // Leímos y está bloqueado
    data object Unlocked : AuthState  // Leímos y es libre
}