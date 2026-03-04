package com.geofencetrigger.domain.repository

import com.geofencetrigger.domain.model.GeofenceEvent
import kotlinx.coroutines.flow.Flow

interface GeofenceEventRepository {
    fun getAllEvents(): Flow<List<GeofenceEvent>>
    suspend fun insertEvent(event: GeofenceEvent): Long
    suspend fun markWebhookSent(eventId: Long)
    suspend fun markWebhookFailed(eventId: Long, error: String)
    suspend fun clearAllEvents()
}
