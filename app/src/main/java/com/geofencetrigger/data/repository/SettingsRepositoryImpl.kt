package com.geofencetrigger.data.repository

import com.geofencetrigger.data.local.SettingsDataStore
import com.geofencetrigger.domain.model.AppSettings
import com.geofencetrigger.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> = dataStore.settings

    override suspend fun updateWebhookUrl(url: String) = dataStore.updateWebhookUrl(url)

    override suspend fun updateAuthToken(token: String) = dataStore.updateAuthToken(token)

    override suspend fun updateDeviceName(name: String) = dataStore.updateDeviceName(name)

    override suspend fun updateServiceEnabled(enabled: Boolean) = dataStore.updateServiceEnabled(enabled)
}
