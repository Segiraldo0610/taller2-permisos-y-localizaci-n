package com.sebastianysamuel.taller2.ui.map

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapViewModelTest {

    @Test
    fun `cada ubicación nueva actualiza el marcador y la ruta`() {
        val viewModel = MapViewModel()
        val first = RoutePoint(4.6285, -74.0649)
        val second = RoutePoint(4.6290, -74.0652)

        viewModel.onLocation(first)
        viewModel.onLocation(second)

        assertEquals(second, viewModel.uiState.currentLocation)
        assertEquals(listOf(first, second), viewModel.uiState.route)
    }

    @Test
    fun `una ubicación repetida no duplica la ruta`() {
        val viewModel = MapViewModel()
        val point = RoutePoint(4.6285, -74.0649)

        viewModel.onLocation(point)
        viewModel.onLocation(point)

        assertEquals(listOf(point), viewModel.uiState.route)
    }

    @Test
    fun `el valor de luz actualiza el estilo del mapa`() {
        val viewModel = MapViewModel()

        viewModel.onLux(lux = 10f, systemDark = false)

        assertEquals(10f, viewModel.uiState.lux)
        assertEquals(MapThemeMode.DARK, viewModel.uiState.themeMode)
    }

    @Test
    fun `no inicia búsqueda con una dirección vacía`() {
        val viewModel = MapViewModel()
        viewModel.onSearchQueryChanged("   ")

        assertNull(viewModel.beginSearch())
        assertFalse(viewModel.uiState.isSearching)
    }

    @Test
    fun `inicia búsqueda con la dirección sin espacios externos`() {
        val viewModel = MapViewModel()
        viewModel.onSearchQueryChanged("  Universidad Javeriana  ")

        assertEquals("Universidad Javeriana", viewModel.beginSearch())
        assertTrue(viewModel.uiState.isSearching)
    }

    @Test
    fun `un resultado de búsqueda crea marcador y termina la carga`() {
        val viewModel = MapViewModel()
        val place = MapMarker(RoutePoint(4.6285, -74.0649), "Universidad Javeriana")
        viewModel.onSearchQueryChanged("Universidad Javeriana")
        viewModel.beginSearch()

        viewModel.onSearchResult(place)

        assertEquals(place, viewModel.uiState.searchMarker)
        assertFalse(viewModel.uiState.isSearching)
    }
}
