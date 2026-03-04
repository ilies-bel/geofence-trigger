package com.geofencetrigger.presentation.screen.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geofencetrigger.domain.model.GeofenceZone
import com.geofencetrigger.domain.repository.GeofenceZoneRepository
import com.geofencetrigger.service.GeofenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddZoneDialogState(
    val isVisible: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val name: String = "",
    val radius: String = "100"
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val zoneRepository: GeofenceZoneRepository,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    val zones: StateFlow<List<GeofenceZone>> = zoneRepository.getAllZones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dialogState = MutableStateFlow(AddZoneDialogState())
    val dialogState: StateFlow<AddZoneDialogState> = _dialogState.asStateFlow()

    fun showAddDialog(lat: Double, lng: Double) {
        _dialogState.value = AddZoneDialogState(
            isVisible = true,
            latitude = lat,
            longitude = lng
        )
    }

    fun dismissDialog() {
        _dialogState.value = AddZoneDialogState()
    }

    fun updateDialogName(name: String) {
        _dialogState.value = _dialogState.value.copy(name = name)
    }

    fun updateDialogRadius(radius: String) {
        _dialogState.value = _dialogState.value.copy(radius = radius)
    }

    fun addZone() {
        val state = _dialogState.value
        val radiusMeters = state.radius.toFloatOrNull() ?: return

        val zone = GeofenceZone(
            id = UUID.randomUUID().toString(),
            name = state.name.ifBlank { "Zone" },
            latitude = state.latitude,
            longitude = state.longitude,
            radiusMeters = radiusMeters
        )

        viewModelScope.launch {
            zoneRepository.insertZone(zone)
            geofenceManager.registerGeofence(zone)
            dismissDialog()
        }
    }
}
