package com.geofencetrigger.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.geofencetrigger.domain.model.GeofenceZone

@Entity(tableName = "geofence_zones")
data class GeofenceZoneEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val isActive: Boolean,
    val createdAt: Long
) {
    fun toDomain(): GeofenceZone = GeofenceZone(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        radiusMeters = radiusMeters,
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(zone: GeofenceZone): GeofenceZoneEntity = GeofenceZoneEntity(
            id = zone.id,
            name = zone.name,
            latitude = zone.latitude,
            longitude = zone.longitude,
            radiusMeters = zone.radiusMeters,
            isActive = zone.isActive,
            createdAt = zone.createdAt
        )
    }
}
