package com.geofencetrigger.domain.repository

import com.geofencetrigger.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateWebhookUrl(url: String)
    suspend fun updateAuthToken(token: String)
    suspend fun updateDeviceName(name: String)
    suspend fun updateServiceEnabled(enabled: Boolean)
}
