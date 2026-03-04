package com.geofencetrigger.presentation.screen.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geofencetrigger.domain.model.AppSettings
import com.geofencetrigger.domain.repository.SettingsRepository
import com.geofencetrigger.service.GeofenceForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun updateWebhookUrl(url: String) {
        viewModelScope.launch { settingsRepository.updateWebhookUrl(url) }
    }

    fun updateAuthToken(token: String) {
        viewModelScope.launch { settingsRepository.updateAuthToken(token) }
    }

    fun updateDeviceName(name: String) {
        viewModelScope.launch { settingsRepository.updateDeviceName(name) }
    }

    fun toggleService(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateServiceEnabled(enabled)
            if (enabled) {
                GeofenceForegroundService.start(context)
            } else {
                GeofenceForegroundService.stop(context)
            }
        }
    }
}
