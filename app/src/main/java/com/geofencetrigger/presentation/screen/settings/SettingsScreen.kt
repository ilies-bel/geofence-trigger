package com.geofencetrigger.presentation.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(title = { Text("Settings") })

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Webhook Configuration",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = settings.webhookUrl,
                onValueChange = viewModel::updateWebhookUrl,
                label = { Text("Webhook URL") },
                placeholder = { Text("https://example.com/webhook") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = settings.authToken,
                onValueChange = viewModel::updateAuthToken,
                label = { Text("Authorization Token") },
                placeholder = { Text("Bearer token (without 'Bearer ' prefix)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = settings.deviceName,
                onValueChange = viewModel::updateDeviceName,
                label = { Text("Device Name") },
                placeholder = { Text("my-phone") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Service",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        ListItem(
            headlineContent = { Text("Enable Geofence Monitoring") },
            supportingContent = {
                Text("Runs a foreground service to monitor geofences reliably")
            },
            trailingContent = {
                Switch(
                    checked = settings.isServiceEnabled,
                    onCheckedChange = viewModel::toggleService
                )
            }
        )
    }
}
