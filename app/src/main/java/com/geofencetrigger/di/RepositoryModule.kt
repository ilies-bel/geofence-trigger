package com.geofencetrigger.di

import com.geofencetrigger.data.repository.GeofenceEventRepositoryImpl
import com.geofencetrigger.data.repository.GeofenceZoneRepositoryImpl
import com.geofencetrigger.data.repository.SettingsRepositoryImpl
import com.geofencetrigger.domain.repository.GeofenceEventRepository
import com.geofencetrigger.domain.repository.GeofenceZoneRepository
import com.geofencetrigger.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindZoneRepository(impl: GeofenceZoneRepositoryImpl): GeofenceZoneRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: GeofenceEventRepositoryImpl): GeofenceEventRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
