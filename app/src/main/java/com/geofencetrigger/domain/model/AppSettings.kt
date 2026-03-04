package com.geofencetrigger.domain.model

data class AppSettings(
    val webhookUrl: String = "",
    val authToken: String = "",
    val deviceName: String = "",
    val isServiceEnabled: Boolean = false
)
