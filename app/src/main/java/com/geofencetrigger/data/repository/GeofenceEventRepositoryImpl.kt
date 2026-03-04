package com.geofencetrigger.data.repository

import com.geofencetrigger.data.local.dao.GeofenceEventDao
import com.geofencetrigger.data.local.entity.GeofenceEventEntity
import com.geofencetrigger.domain.model.GeofenceEvent
import com.geofencetrigger.domain.repository.GeofenceEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceEventRepositoryImpl @Inject constructor(
    private val dao: GeofenceEventDao
) : GeofenceEventRepository {

    override fun getAllEvents(): Flow<List<GeofenceEvent>> =
        dao.getAllEvents().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertEvent(event: GeofenceEvent): Long =
        dao.insertEvent(GeofenceEventEntity.fromDomain(event))

    override suspend fun markWebhookSent(eventId: Long) =
        dao.markWebhookSent(eventId)

    override suspend fun markWebhookFailed(eventId: Long, error: String) =
        dao.markWebhookFailed(eventId, error)

    override suspend fun clearAllEvents() =
        dao.clearAllEvents()
}
