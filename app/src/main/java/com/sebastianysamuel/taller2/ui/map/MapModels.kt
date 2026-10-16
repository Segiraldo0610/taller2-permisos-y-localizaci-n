package com.sebastianysamuel.taller2.ui.map

enum class MapThemeMode {
    LIGHT,
    DARK,
}

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
)

data class MapMarker(
    val point: RoutePoint,
    val title: String,
)

enum class MapMessage {
    LOCATION_PERMISSION_DENIED,
    LOCATION_UNAVAILABLE,
    ADDRESS_NOT_FOUND,
    GEOCODING_UNAVAILABLE,
}

data class MapUiState(
    val currentLocation: RoutePoint? = null,
    val route: List<RoutePoint> = emptyList(),
    val searchQuery: String = "",
    val searchMarker: MapMarker? = null,
    val selectedMarker: MapMarker? = null,
    val themeMode: MapThemeMode = MapThemeMode.LIGHT,
    val lux: Float? = null,
    val isSearching: Boolean = false,
    val message: MapMessage? = null,
)

fun themeModeForLux(
    lux: Float?,
    systemDark: Boolean,
): MapThemeMode = when {
    lux == null -> if (systemDark) MapThemeMode.DARK else MapThemeMode.LIGHT
    lux < 50f -> MapThemeMode.DARK
    else -> MapThemeMode.LIGHT
}

fun appendRoutePoint(
    route: List<RoutePoint>,
    point: RoutePoint,
): List<RoutePoint> = if (route.lastOrNull() == point) route else route + point
