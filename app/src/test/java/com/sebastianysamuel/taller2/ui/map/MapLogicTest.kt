package com.sebastianysamuel.taller2.ui.map

import org.junit.Assert.assertEquals
import org.junit.Test

class MapLogicTest {

    @Test
    fun `usa estilo oscuro cuando hay menos de cincuenta lux`() {
        assertEquals(MapThemeMode.DARK, themeModeForLux(lux = 49.9f, systemDark = false))
    }

    @Test
    fun `usa estilo claro desde cincuenta lux`() {
        assertEquals(MapThemeMode.LIGHT, themeModeForLux(lux = 50f, systemDark = true))
    }

    @Test
    fun `usa el tema del sistema cuando el dispositivo no tiene sensor`() {
        assertEquals(MapThemeMode.DARK, themeModeForLux(lux = null, systemDark = true))
        assertEquals(MapThemeMode.LIGHT, themeModeForLux(lux = null, systemDark = false))
    }

    @Test
    fun `no agrega dos ubicaciones consecutivas iguales`() {
        val point = RoutePoint(latitude = 4.6285, longitude = -74.0649)

        assertEquals(listOf(point), appendRoutePoint(listOf(point), point))
    }

    @Test
    fun `agrega una ubicación diferente al final del recorrido`() {
        val first = RoutePoint(latitude = 4.6285, longitude = -74.0649)
        val second = RoutePoint(latitude = 4.6290, longitude = -74.0652)

        assertEquals(listOf(first, second), appendRoutePoint(listOf(first), second))
    }
}
