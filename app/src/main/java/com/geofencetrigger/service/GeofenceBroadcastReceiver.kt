package com.geofencetrigger.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e(TAG, "Null geofencing event")
            return
        }

        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Geofencing error: ${geofencingEvent.errorCode}")
            return
        }

        val transitionType = geofencingEvent.geofenceTransition
        val eventTypeStr = when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
            else -> {
                Log.w(TAG, "Unknown transition type: $transitionType")
                return
            }
        }

        val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return

        for (geofence in triggeringGeofences) {
            val workData = Data.Builder()
                .putString(WebhookWorker.KEY_ZONE_ID, geofence.requestId)
                .putString(WebhookWorker.KEY_EVENT_TYPE, eventTypeStr)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<WebhookWorker>()
                .setInputData(workData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
            Log.d(TAG, "Enqueued webhook work for zone ${geofence.requestId} ($eventTypeStr)")
        }
    }

    companion object {
        private const val TAG = "GeofenceReceiver"
    }
}
