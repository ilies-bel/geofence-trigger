package com.geofencetrigger.domain.model

data class GeofenceEvent(
    val id: Long = 0,
    val zoneId: String,
    val zoneName: String,
    val eventType: EventType,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val timestamp: String,
    val webhookSent: Boolean = false,
    val webhookError: String? = null
)

enum class EventType {
    ENTER, EXIT
}
