package com.geofencetrigger.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.geofencetrigger.data.local.dao.GeofenceEventDao
import com.geofencetrigger.data.local.dao.GeofenceZoneDao
import com.geofencetrigger.data.local.entity.GeofenceEventEntity
import com.geofencetrigger.data.remote.WebhookService
import com.geofencetrigger.data.remote.dto.WebhookPayload
import com.geofencetrigger.domain.model.EventType
import com.geofencetrigger.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@HiltWorker
class WebhookWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val zoneDao: GeofenceZoneDao,
    private val eventDao: GeofenceEventDao,
    private val webhookService: WebhookService,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val zoneId = inputData.getString(KEY_ZONE_ID) ?: return Result.failure()
        val eventTypeStr = inputData.getString(KEY_EVENT_TYPE) ?: return Result.failure()
        val eventType = EventType.valueOf(eventTypeStr)

        val zone = zoneDao.getZoneById(zoneId)
        if (zone == null) {
            Log.w(TAG, "Zone $zoneId not found in database")
            return Result.failure()
        }

        val settings = settingsRepository.getSettings().first()
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC))

        val eventEntity = GeofenceEventEntity(
            zoneId = zoneId,
            zoneName = zone.name,
            eventType = eventType.name,
            latitude = zone.latitude,
            longitude = zone.longitude,
            radius = zone.radiusMeters,
            timestamp = timestamp,
            webhookSent = false,
            webhookError = null
        )
        val eventId = eventDao.insertEvent(eventEntity)

        if (settings.webhookUrl.isBlank()) {
            Log.w(TAG, "No webhook URL configured")
            eventDao.markWebhookFailed(eventId, "No webhook URL configured")
            return Result.success()
        }

        val payload = WebhookPayload(
            event = eventType.name.lowercase(),
            zoneName = zone.name,
            zoneId = zoneId,
            latitude = zone.latitude,
            longitude = zone.longitude,
            radius = zone.radiusMeters,
            timestamp = timestamp,
            deviceId = settings.deviceName.ifBlank { "unknown" }
        )

        return try {
            val authHeader = if (settings.authToken.isNotBlank()) {
                "Bearer ${settings.authToken}"
            } else {
                ""
            }

            val response = webhookService.sendWebhook(
                url = settings.webhookUrl,
                authHeader = authHeader,
                payload = payload
            )

            if (response.isSuccessful) {
                eventDao.markWebhookSent(eventId)
                Log.d(TAG, "Webhook sent for $zoneId ($eventTypeStr)")
                Result.success()
            } else {
                val error = "HTTP ${response.code()}: ${response.message()}"
                eventDao.markWebhookFailed(eventId, error)
                Log.e(TAG, "Webhook failed: $error")
                if (runAttemptCount < 3) Result.retry() else Result.failure()
            }
        } catch (e: Exception) {
            val error = e.message ?: "Unknown error"
            eventDao.markWebhookFailed(eventId, error)
            Log.e(TAG, "Webhook exception: $error", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        private const val TAG = "WebhookWorker"
        const val KEY_ZONE_ID = "zone_id"
        const val KEY_EVENT_TYPE = "event_type"
    }
}
