package com.geofencetrigger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.geofencetrigger.data.local.entity.GeofenceZoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceZoneDao {

    @Query("SELECT * FROM geofence_zones ORDER BY createdAt DESC")
    fun getAllZones(): Flow<List<GeofenceZoneEntity>>

    @Query("SELECT * FROM geofence_zones WHERE id = :id")
    suspend fun getZoneById(id: String): GeofenceZoneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(zone: GeofenceZoneEntity)

    @Update
    suspend fun updateZone(zone: GeofenceZoneEntity)

    @Query("DELETE FROM geofence_zones WHERE id = :id")
    suspend fun deleteZone(id: String)

    @Query("SELECT * FROM geofence_zones WHERE isActive = 1")
    suspend fun getActiveZones(): List<GeofenceZoneEntity>
}
