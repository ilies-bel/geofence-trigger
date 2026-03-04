package com.geofencetrigger.domain.repository

import com.geofencetrigger.domain.model.GeofenceZone
import kotlinx.coroutines.flow.Flow

interface GeofenceZoneRepository {
    fun getAllZones(): Flow<List<GeofenceZone>>
    suspend fun getZoneById(id: String): GeofenceZone?
    suspend fun insertZone(zone: GeofenceZone)
    suspend fun updateZone(zone: GeofenceZone)
    suspend fun deleteZone(id: String)
}
