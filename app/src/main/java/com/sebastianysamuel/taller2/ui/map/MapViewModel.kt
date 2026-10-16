package com.sebastianysamuel.taller2.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {
    var uiState by mutableStateOf(MapUiState())
        private set

    fun onLocation(point: RoutePoint) {
        uiState = uiState.copy(
            currentLocation = point,
            route = appendRoutePoint(uiState.route, point),
        )
    }

    fun onLux(lux: Float?, systemDark: Boolean) {
        uiState = uiState.copy(
            lux = lux,
            themeMode = themeModeForLux(lux, systemDark),
        )
    }

    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun beginSearch(): String? {
        val query = uiState.searchQuery.trim()
        if (query.isEmpty()) return null
        uiState = uiState.copy(isSearching = true, message = null)
        return query
    }

    fun onSearchResult(marker: MapMarker?) {
        uiState = uiState.copy(
            searchMarker = marker,
            isSearching = false,
            message = if (marker == null) MapMessage.ADDRESS_NOT_FOUND else null,
        )
    }

    fun onSelectedAddress(point: RoutePoint, address: String?) {
        uiState = uiState.copy(
            selectedMarker = MapMarker(point, address.orEmpty()),
            message = if (address == null) MapMessage.ADDRESS_NOT_FOUND else null,
        )
    }

    fun onPermissionDenied() {
        uiState = uiState.copy(message = MapMessage.LOCATION_PERMISSION_DENIED)
    }

    fun onLocationUnavailable() {
        uiState = uiState.copy(message = MapMessage.LOCATION_UNAVAILABLE)
    }

    fun onGeocodingUnavailable() {
        uiState = uiState.copy(
            isSearching = false,
            message = MapMessage.GEOCODING_UNAVAILABLE,
        )
    }

    fun clearMessage() {
        uiState = uiState.copy(message = null)
    }
}
