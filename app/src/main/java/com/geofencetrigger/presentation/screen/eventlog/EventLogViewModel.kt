package com.geofencetrigger.presentation.screen.eventlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geofencetrigger.domain.model.GeofenceEvent
import com.geofencetrigger.domain.repository.GeofenceEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventLogViewModel @Inject constructor(
    private val eventRepository: GeofenceEventRepository
) : ViewModel() {

    val events: StateFlow<List<GeofenceEvent>> = eventRepository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearEvents() {
        viewModelScope.launch { eventRepository.clearAllEvents() }
    }
}
