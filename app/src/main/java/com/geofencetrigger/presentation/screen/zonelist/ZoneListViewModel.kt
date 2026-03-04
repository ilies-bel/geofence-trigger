package com.geofencetrigger.presentation.screen.zonelist

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
import javax.inject.Inject

data class EditZoneDialogState(
    val isVisible: Boolean = false,
    val zone: GeofenceZone? = null,
    val name: String = "",
    val radius: String = ""
)

@HiltViewModel
class ZoneListViewModel @Inject constructor(
    private val zoneRepository: GeofenceZoneRepository,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    val zones: StateFlow<List<GeofenceZone>> = zoneRepository.getAllZones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editState = MutableStateFlow(EditZoneDialogState())
    val editState: StateFlow<EditZoneDialogState> = _editState.asStateFlow()

    fun deleteZone(zone: GeofenceZone) {
        viewModelScope.launch {
            geofenceManager.unregisterGeofence(zone.id)
            zoneRepository.deleteZone(zone.id)
        }
    }

    fun showEditDialog(zone: GeofenceZone) {
        _editState.value = EditZoneDialogState(
            isVisible = true,
            zone = zone,
            name = zone.name,
            radius = zone.radiusMeters.toInt().toString()
        )
    }

    fun dismissEditDialog() {
        _editState.value = EditZoneDialogState()
    }

    fun updateEditName(name: String) {
        _editState.value = _editState.value.copy(name = name)
    }

    fun updateEditRadius(radius: String) {
        _editState.value = _editState.value.copy(radius = radius)
    }

    fun saveEdit() {
        val state = _editState.value
        val zone = state.zone ?: return
        val radiusMeters = state.radius.toFloatOrNull() ?: return

        val updatedZone = zone.copy(
            name = state.name.ifBlank { zone.name },
            radiusMeters = radiusMeters
        )

        viewModelScope.launch {
            geofenceManager.unregisterGeofence(zone.id)
            zoneRepository.updateZone(updatedZone)
            geofenceManager.registerGeofence(updatedZone)
            dismissEditDialog()
        }
    }
}
