package com.geofencetrigger.domain.model

data class GeofenceZone(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
