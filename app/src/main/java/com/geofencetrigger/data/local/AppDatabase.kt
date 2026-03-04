package com.geofencetrigger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.geofencetrigger.data.local.dao.GeofenceEventDao
import com.geofencetrigger.data.local.dao.GeofenceZoneDao
import com.geofencetrigger.data.local.entity.GeofenceEventEntity
import com.geofencetrigger.data.local.entity.GeofenceZoneEntity

@Database(
    entities = [GeofenceZoneEntity::class, GeofenceEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun geofenceZoneDao(): GeofenceZoneDao
    abstract fun geofenceEventDao(): GeofenceEventDao
}
