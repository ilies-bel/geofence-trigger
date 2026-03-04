package com.geofencetrigger.presentation.screen.eventlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geofencetrigger.domain.model.EventType
import com.geofencetrigger.domain.model.GeofenceEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventLogScreen(viewModel: EventLogViewModel = hiltViewModel()) {
    val events by viewModel.events.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Event Log") },
            actions = {
                if (events.isNotEmpty()) {
                    IconButton(onClick = viewModel::clearEvents) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all")
                    }
                }
            }
        )

        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No events recorded yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(events, key = { it.id }) { event ->
                    EventLogItem(event = event)
                }
            }
        }
    }
}

@Composable
private fun EventLogItem(event: GeofenceEvent) {
    val (icon, iconColor) = when (event.eventType) {
        EventType.ENTER -> Icons.Default.Login to Color(0xFF4CAF50)
        EventType.EXIT -> Icons.Default.Logout to Color(0xFFF44336)
    }

    ListItem(
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = event.eventType.name,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        },
        headlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(event.zoneName)
                Text(
                    text = event.eventType.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = iconColor
                )
            }
        },
        supportingContent = {
            Column {
                Text(
                    text = event.timestamp,
                    style = MaterialTheme.typography.bodySmall
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.webhookSent) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Sent",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(14.dp).padding(end = 4.dp)
                        )
                        Text("Webhook sent", style = MaterialTheme.typography.bodySmall)
                    } else if (event.webhookError != null) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(14.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = event.webhookError,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336)
                        )
                    } else {
                        Text("Webhook pending", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    )
}
