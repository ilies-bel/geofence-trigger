package com.geofencetrigger.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebhookPayload(
    val event: String,
    @SerialName("zone_name") val zoneName: String,
    @SerialName("zone_id") val zoneId: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val timestamp: String,
    @SerialName("device_id") val deviceId: String
)
