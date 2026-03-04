package com.geofencetrigger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.geofencetrigger.domain.model.EventType
import com.geofencetrigger.domain.model.GeofenceEvent

@Entity(tableName = "geofence_events")
data class GeofenceEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zoneId: String,
    val zoneName: String,
    val eventType: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val timestamp: String,
    val webhookSent: Boolean,
    val webhookError: String?
) {
    fun toDomain(): GeofenceEvent = GeofenceEvent(
        id = id,
        zoneId = zoneId,
        zoneName = zoneName,
        eventType = EventType.valueOf(eventType),
        latitude = latitude,
        longitude = longitude,
        radius = radius,
        timestamp = timestamp,
        webhookSent = webhookSent,
        webhookError = webhookError
    )

    companion object {
        fun fromDomain(event: GeofenceEvent): GeofenceEventEntity = GeofenceEventEntity(
            id = event.id,
            zoneId = event.zoneId,
            zoneName = event.zoneName,
            eventType = event.eventType.name,
            latitude = event.latitude,
            longitude = event.longitude,
            radius = event.radius,
            timestamp = event.timestamp,
            webhookSent = event.webhookSent,
            webhookError = event.webhookError
        )
    }
}
