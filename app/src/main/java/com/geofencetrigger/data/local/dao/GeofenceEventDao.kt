package com.geofencetrigger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.geofencetrigger.data.local.entity.GeofenceEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceEventDao {

    @Query("SELECT * FROM geofence_events ORDER BY id DESC")
    fun getAllEvents(): Flow<List<GeofenceEventEntity>>

    @Insert
    suspend fun insertEvent(event: GeofenceEventEntity): Long

    @Query("UPDATE geofence_events SET webhookSent = 1, webhookError = NULL WHERE id = :eventId")
    suspend fun markWebhookSent(eventId: Long)

    @Query("UPDATE geofence_events SET webhookSent = 0, webhookError = :error WHERE id = :eventId")
    suspend fun markWebhookFailed(eventId: Long, error: String)

    @Query("DELETE FROM geofence_events")
    suspend fun clearAllEvents()
}
