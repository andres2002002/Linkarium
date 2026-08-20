package com.habitiora.linkarium.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitiora.linkarium.core.UriUtils.toUriSafe
import com.habitiora.linkarium.core.UriValidator
import com.habitiora.linkarium.core.exporters.ImportStatus
import com.habitiora.linkarium.data.exporters.DeleteDataUserManager
import com.habitiora.linkarium.data.local.usecase.ImportUseCase
import com.habitiora.linkarium.data.repository.PreferencesRepository
import com.habitiora.linkarium.ui.navigation.Screens
import com.habitiora.linkarium.ui.scaffold.dialogs.DialogType
import com.habitiora.linkarium.ui.scaffold.dialogs.MessageValues
import com.habitiora.linkarium.ui.utils.pubsAndSubs.MessageBus
import com.habitiora.linkarium.ui.utils.pubsAndSubs.NavigationEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val messageBus: MessageBus,
    private val navigationEventBus: NavigationEventBus,
    private val preferences: PreferencesRepository,
    private val uriValidator: UriValidator,
    private val importUseCase: ImportUseCase,
    private val deleteDataUserManager: DeleteDataUserManager,
) : ViewModel() {
    val isBiometricLockEnabled: StateFlow<Boolean> = preferences.userPreferences.map { it.useBiometricLock }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun updateBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            preferences.updateBiometricLock(enabled)
        }
    }
    fun navigateTo(screen: Screens) = navigationEventBus.navigate(screen)

    fun openUri(uri: String, openUri: (Uri) -> Unit) {
        if (uriValidator.isValidResource(uri)) {
            uri.toUriSafe()?.let {
                openUri(it)
            }
        }
    }

    fun import(uri: Uri) {
        viewModelScope.launch {
            try {
                importUseCase(uri).collect { status ->
                    if (status == ImportStatus.InProgress){
                        messageBus.pubMessage(MessageValues(DialogType.Loading))
                    }
                    else {
                        messageBus.pubMessage(null)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
    fun deleteUserData(){
        viewModelScope.launch {
            try {
                val mssg = MessageValues(
                    DialogType.Warning,
                    title = "Delete User Data",
                    message = "Are you sure you want to delete All User Data?",
                    details = "This Action is irreversible",
                    buttons = mapOf(
                        "delete" to {
                            viewModelScope.launch {
                                deleteDataUserManager.delete()
                                messageBus.pubMessage(null)
                                Timber.i("User Data Deleted")
                            }
                        },
                        "cancel" to {
                            messageBus.pubMessage(null)
                            Timber.d("Delete user data cancelled")
                        }
                    )
                )
                messageBus.pubMessage(mssg)
            } catch (e: Exception){
                Timber.e(e)
            }
        }
    }

}