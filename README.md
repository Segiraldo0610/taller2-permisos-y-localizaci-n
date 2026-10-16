# Taller 2 Permisos localización y mapas en Android con Compose

Repositorio de trabajo para el Taller 2 de Computación móvil de la Pontificia Universidad Javeriana.

**Entrega:** viernes 16 de octubre de 2026 a las 11:59 p. m.  
**Modalidad:** individual o en grupos de dos. Se permiten varios envíos, pero el Campus conserva únicamente el último.

**Integrantes:** Sebastián Peralta Neme y Samuel Giraldo.

## Estado de implementación

Las funcionalidades exigidas están implementadas en código. Antes de entregar todavía se deben ejecutar las pruebas manuales indicadas al final de este documento en el celular o emulador usado para el video.

- [x] Proyecto Android compilable con Kotlin y Jetpack Compose.
- [x] Arquitectura Single Activity; `MainActivity.kt` no declara composables.
- [x] Navigation 3 con destinos Inicio, Multimedia y Mapa.
- [x] Temas claro y oscuro y textos visibles en `strings.xml`.
- [x] Selector Foto/Video.
- [x] Captura de foto y video mediante Activity Result y `FileProvider`.
- [x] Selección de foto y video mediante Photo Picker.
- [x] Vista previa centrada con tamaño fijo.
- [x] Ktor 3.5.2 declarado y punto de creación centralizado en `HttpClientProvider`.
- [x] Mapa con marcador de ubicación actual y manejo del permiso en tiempo de ejecución.
- [x] Cambio de estilo del mapa con el sensor de luminosidad.
- [x] Recorrido mediante `Polyline` a partir de actualizaciones de ubicación.
- [x] Búsqueda de direcciones mediante geocodificación directa.
- [x] Marcador por pulsación prolongada con geocodificación inversa.

La implementación de Sebastián se desarrolla en la rama `feature/multimedia-sebastian`.
La integración de mapas y entrega se desarrolla en la rama `feature/mapas-samuel`.
El contraste detallado entre la implementación, la rúbrica y las pruebas pendientes está en
[`docs/VERIFICACION_RUBRICA.md`](docs/VERIFICACION_RUBRICA.md).

## Objetivo

Construir una aplicación Android en Kotlin y Jetpack Compose que integre captura y selección de contenido multimedia, permisos en tiempo de ejecución, localización, Google Maps, sensor de luminosidad, trazado de recorridos y geocodificación directa e inversa.

## Rúbrica funcional

| Funcionalidad evaluada | Valor |
|---|---:|
| Tomar fotos y grabar videos con permiso de cámara; mostrar el resultado dentro de la aplicación | 25 % |
| Seleccionar fotos y videos desde la galería; mostrar el resultado dentro de la aplicación | 25 % |
| Mostrar Google Maps con un marcador en la ubicación actual | 10 % |
| Cambiar entre estilo claro y oscuro según el sensor de luminosidad | 10 % |
| Dibujar una `Polyline` con la ruta conforme cambia la ubicación | 10 % |
| Geocoding: dirección escrita → coordenadas, marcador y movimiento de cámara | 10 % |
| Geocoding reverso: pulsación prolongada → dirección usada como título del marcador | 10 % |
| **Total** | **100 %** |

## Flujo mínimo de la aplicación

1. La pantalla principal presenta dos botones con iconos o imágenes.
2. El primer botón abre la pantalla multimedia.
   - Un selector cambia entre modo Foto y modo Video.
   - Permite capturar con la cámara o seleccionar desde la galería.
   - El contenido se muestra centrado y con tamaño fijo.
3. El segundo botón abre la pantalla de mapas.
   - Solicita ubicación y muestra la posición actual.
   - Cambia el estilo del mapa con el sensor de luminosidad.
   - Dibuja el recorrido con una `Polyline`.
   - Busca direcciones escritas con `Geocoder`.
   - En una pulsación prolongada, realiza geocoding reverso y crea un marcador.

## Requisitos obligatorios

- Kotlin y Jetpack Compose.
- Arquitectura Single Activity.
- `MainActivity.kt` no debe contener composables.
- Todos los textos visibles deben estar en `res/values/strings.xml`.
- Navigation 3 versión 1.1.6 o superior.
- Ktor versión 3.5.2 o superior.
- Tema claro y oscuro.
- Se recomienda crear composables reutilizables.

## Configuración recomendada

Las versiones siguientes cumplen la rúbrica y corresponden a versiones publicadas al momento de preparar el repositorio:

| Componente | Versión |
|---|---:|
| `compileSdk` | 37 |
| `minSdk` | 23 o superior |
| Navigation 3 | 1.1.7 |
| Ktor | 3.5.2 |
| Maps Compose | 8.4.0 |
| Google Play Services Location | 21.4.0 |

Dependencias centrales para `gradle/libs.versions.toml`:

```toml
[versions]
navigation3 = "1.1.7"
ktor = "3.5.2"
mapsCompose = "8.4.0"
playServicesLocation = "21.4.0"

[libraries]
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "navigation3" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "navigation3" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-android = { module = "io.ktor:ktor-client-android", version.ref = "ktor" }
maps-compose = { module = "com.google.maps.android:maps-compose", version.ref = "mapsCompose" }
play-services-location = { module = "com.google.android.gms:play-services-location", version.ref = "playServicesLocation" }
```

En `app/build.gradle.kts`:

```kotlin
android {
    compileSdk = 37

    defaultConfig {
        minSdk = 23
    }
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
}
```

Permisos mínimos en `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

Para seleccionar fotos y videos se recomienda el Photo Picker de Android, que no solicita acceso general a toda la galería. El permiso de cámara y los permisos de ubicación sí deben solicitarse durante la ejecución.

## Compilación local

El proyecto incluye Gradle Wrapper. Desde la raíz del repositorio:

```powershell
.\gradlew.bat :app:assembleDebug
```

El APK resultante se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## API key de Google Maps

Crea una clave en Google Cloud, habilita Maps SDK for Android y agrégala al proyecto, por ejemplo en `local.properties`:

```properties
MAPS_API_KEY=TU_API_KEY
```

El proyecto lee esa propiedad desde `app/build.gradle.kts` y la expone al `AndroidManifest.xml` mediante un `manifestPlaceholder`. `local.properties` está ignorado por Git y la clave no debe subirse al repositorio.

## Estructura sugerida

```text
app/src/main/java/.../
├── MainActivity.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── AppRoute.kt
├── ui/
│   ├── home/HomeScreen.kt
│   ├── media/
│   │   ├── MediaScreen.kt
│   │   ├── MediaViewModel.kt
│   │   └── MediaFileProvider.kt
│   └── map/
│       ├── MapScreen.kt
│       ├── MapViewModel.kt
│       ├── LocationTracker.kt
│       ├── LightSensorObserver.kt
│       └── GeocodingService.kt
└── network/HttpClientProvider.kt
```

## Entregables

1. Código fuente en formato ZIP o RAR.
2. APK generado en `app/build/outputs/apk/debug/app-debug.apk`.
3. Video de máximo seis minutos publicado en YouTube con acceso no privado. Debe demostrar las funcionalidades y explicar:
   - estructura de carpetas o paquetes;
   - composables y pantallas;
   - implementación de cámara mediante Intent o CameraX;
   - implementación de Geocoder;
   - partes del código responsables de cada requisito.

El orden sugerido para la grabación está en [`docs/GUION_VIDEO.md`](docs/GUION_VIDEO.md).

## Lista de control antes de entregar

- [ ] La aplicación compila y abre sin cerrarse inesperadamente.
- [ ] Foto y video funcionan tanto desde cámara como desde galería.
- [ ] La previsualización conserva un tamaño fijo.
- [ ] Los permisos rechazados muestran un estado comprensible.
- [ ] El mapa centra y marca la ubicación actual.
- [ ] El estilo cambia con luz alta y baja.
- [ ] La ruta se actualiza con una `Polyline`.
- [ ] La búsqueda textual crea un marcador y mueve la cámara.
- [ ] El LongClick crea un marcador con una dirección legible.
- [ ] Se generaron ZIP/RAR, APK y enlace de video.

La correspondencia entre cada criterio, el código y su prueba manual está documentada en [`docs/VERIFICACION_RUBRICA.md`](docs/VERIFICACION_RUBRICA.md).
