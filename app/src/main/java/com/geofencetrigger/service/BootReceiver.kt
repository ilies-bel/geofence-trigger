package com.geofencetrigger.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.geofencetrigger.data.local.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = settingsDataStore.settings.first()
                if (settings.isServiceEnabled) {
                    GeofenceForegroundService.start(context)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
