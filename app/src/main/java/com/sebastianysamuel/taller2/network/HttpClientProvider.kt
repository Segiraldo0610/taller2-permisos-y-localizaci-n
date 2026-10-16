package com.sebastianysamuel.taller2.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

/**
 * Punto central para crear clientes Ktor cuando una funcionalidad necesite red.
 * Cada consumidor es responsable de cerrar la instancia que solicite.
 */
object HttpClientProvider {
    fun create(): HttpClient = HttpClient(Android) {
        expectSuccess = true
    }
}
