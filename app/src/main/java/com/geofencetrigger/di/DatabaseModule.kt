package com.geofencetrigger.di

import android.content.Context
import androidx.room.Room
import com.geofencetrigger.data.local.AppDatabase
import com.geofencetrigger.data.local.dao.GeofenceEventDao
import com.geofencetrigger.data.local.dao.GeofenceZoneDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "geofence_trigger.db"
        ).build()

    @Provides
    fun provideZoneDao(db: AppDatabase): GeofenceZoneDao = db.geofenceZoneDao()

    @Provides
    fun provideEventDao(db: AppDatabase): GeofenceEventDao = db.geofenceEventDao()
}
