package com.geofencetrigger.data.repository

import com.geofencetrigger.data.local.dao.GeofenceZoneDao
import com.geofencetrigger.data.local.entity.GeofenceZoneEntity
import com.geofencetrigger.domain.model.GeofenceZone
import com.geofencetrigger.domain.repository.GeofenceZoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceZoneRepositoryImpl @Inject constructor(
    private val dao: GeofenceZoneDao
) : GeofenceZoneRepository {

    override fun getAllZones(): Flow<List<GeofenceZone>> =
        dao.getAllZones().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getZoneById(id: String): GeofenceZone? =
        dao.getZoneById(id)?.toDomain()

    override suspend fun insertZone(zone: GeofenceZone) =
        dao.insertZone(GeofenceZoneEntity.fromDomain(zone))

    override suspend fun updateZone(zone: GeofenceZone) =
        dao.updateZone(GeofenceZoneEntity.fromDomain(zone))

    override suspend fun deleteZone(id: String) =
        dao.deleteZone(id)
}
