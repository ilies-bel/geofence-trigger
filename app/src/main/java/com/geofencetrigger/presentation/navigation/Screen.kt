package com.geofencetrigger.presentation.navigation

sealed class Screen(val route: String) {
    data object Map : Screen("map")
    data object ZoneList : Screen("zone_list")
    data object Settings : Screen("settings")
    data object EventLog : Screen("event_log")
}
