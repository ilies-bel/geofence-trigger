package com.geofencetrigger.service

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.geofencetrigger.data.local.dao.GeofenceZoneDao
import com.geofencetrigger.domain.model.GeofenceZone
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val zoneDao: GeofenceZoneDao
) {
    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    suspend fun registerGeofence(zone: GeofenceZone): Result<Unit> = runCatching {
        if (!hasLocationPermission()) {
            throw SecurityException("Location permission not granted")
        }

        val geofence = Geofence.Builder()
            .setRequestId(zone.id)
            .setCircularRegion(zone.latitude, zone.longitude, zone.radiusMeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).await()
        Log.d(TAG, "Registered geofence: ${zone.name} (${zone.id})")
    }

    suspend fun unregisterGeofence(zoneId: String): Result<Unit> = runCatching {
        geofencingClient.removeGeofences(listOf(zoneId)).await()
        Log.d(TAG, "Unregistered geofence: $zoneId")
    }

    suspend fun reregisterAllGeofences(): Result<Unit> = runCatching {
        val activeZones = zoneDao.getActiveZones()
        if (activeZones.isEmpty()) return@runCatching

        val geofences = activeZones.map { entity ->
            Geofence.Builder()
                .setRequestId(entity.id)
                .setCircularRegion(entity.latitude, entity.longitude, entity.radiusMeters)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).await()
        Log.d(TAG, "Re-registered ${activeZones.size} geofences")
    }

    suspend fun removeAllGeofences(): Result<Unit> = runCatching {
        geofencingClient.removeGeofences(geofencePendingIntent).await()
        Log.d(TAG, "Removed all geofences")
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    companion object {
        private const val TAG = "GeofenceManager"
    }
}
