package com.geofencetrigger.presentation.screen.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val zones by viewModel.zones.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResult.collectAsStateWithLifecycle()
    val searchError by viewModel.searchError.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var tapMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(48.8566, 2.3522), 12f)
    }

    LaunchedEffect(searchResult) {
        searchResult?.let { result ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(result.location, 15f)
            )
        }
    }

    LaunchedEffect(searchError) {
        searchError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearSearchError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search address...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.searchAddress(searchQuery)
                    keyboardController?.hide()
                }
            ),
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.searchAddress(searchQuery)
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter)
                .zIndex(1f)
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapClick = { latLng ->
                if (tapMode) {
                    viewModel.showAddDialog(latLng.latitude, latLng.longitude)
                    tapMode = false
                }
            }
        ) {
            zones.forEach { zone ->
                Marker(
                    state = MarkerState(position = LatLng(zone.latitude, zone.longitude)),
                    title = zone.name,
                    snippet = "Radius: ${zone.radiusMeters.toInt()}m"
                )
                Circle(
                    center = LatLng(zone.latitude, zone.longitude),
                    radius = zone.radiusMeters.toDouble(),
                    strokeColor = Color(0xFF1976D2),
                    strokeWidth = 2f,
                    fillColor = Color(0x301976D2)
                )
            }

            searchResult?.let { result ->
                Marker(
                    state = MarkerState(position = result.location),
                    title = result.address,
                    snippet = "Search result",
                    onInfoWindowClose = { viewModel.clearSearchResult() }
                )
            }
        }

        FloatingActionButton(
            onClick = { tapMode = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = if (tapMode) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.primary
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add zone")
        }

        if (tapMode) {
            Text(
                text = "Tap on the map to place a geofence",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (dialogState.isVisible) {
        AddZoneDialog(
            state = dialogState,
            onNameChange = viewModel::updateDialogName,
            onRadiusChange = viewModel::updateDialogRadius,
            onConfirm = viewModel::addZone,
            onDismiss = viewModel::dismissDialog
        )
    }
}

@Composable
fun AddZoneDialog(
    state: AddZoneDialogState,
    onNameChange: (String) -> Unit,
    onRadiusChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Geofence Zone") },
        text = {
            Column {
                Text(
                    text = "Location: %.5f, %.5f".format(state.latitude, state.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("Zone Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.radius,
                    onValueChange = onRadiusChange,
                    label = { Text("Radius (meters)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = state.name.isNotBlank() && (state.radius.toFloatOrNull() ?: 0f) > 0f
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
