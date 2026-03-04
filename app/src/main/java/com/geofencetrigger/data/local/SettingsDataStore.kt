package com.geofencetrigger.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.geofencetrigger.domain.model.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val WEBHOOK_URL = stringPreferencesKey("webhook_url")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val DEVICE_NAME = stringPreferencesKey("device_name")
        val SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            webhookUrl = prefs[Keys.WEBHOOK_URL] ?: "https://macbook-pro-de-ilies.tailcdd52c.ts.net/hooks/geofence",
            authToken = prefs[Keys.AUTH_TOKEN] ?: "ff6d8bba7c7c8b2dec4f9447f30c69e3b7d58b7b0c2b9cc8",
            deviceName = prefs[Keys.DEVICE_NAME] ?: "ilies-android",
            isServiceEnabled = prefs[Keys.SERVICE_ENABLED] ?: false
        )
    }

    suspend fun updateWebhookUrl(url: String) {
        context.dataStore.edit { it[Keys.WEBHOOK_URL] = url }
    }

    suspend fun updateAuthToken(token: String) {
        context.dataStore.edit { it[Keys.AUTH_TOKEN] = token }
    }

    suspend fun updateDeviceName(name: String) {
        context.dataStore.edit { it[Keys.DEVICE_NAME] = name }
    }

    suspend fun updateServiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SERVICE_ENABLED] = enabled }
    }
}
