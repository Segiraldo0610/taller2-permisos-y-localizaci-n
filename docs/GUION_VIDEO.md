# Guion para el video de entrega

Duración objetivo: entre 5:20 y 5:50. Antes de grabar, activa la ubicación, verifica la API key de Google Maps y prepara una foto y un video cortos en la galería.

## 0:00–0:30 — Presentación

“Somos Sebastián Peralta y Samuel Giraldo. Esta es nuestra solución del Taller 2 de permisos, multimedia y localización, desarrollada en Kotlin con Jetpack Compose y una arquitectura Single Activity.”

Muestra la pantalla principal y señala los dos accesos: Cámara y galería, y Mapa y recorrido.

## 0:30–1:10 — Estructura del proyecto

Muestra Android Studio:

- `MainActivity.kt` solo inicia el tema y la aplicación; no contiene composables.
- `navigation` contiene los tres destinos con Navigation 3.
- `ui/media` reúne pantalla, estado y manejo de archivos multimedia.
- `ui/map` reúne pantalla, estado, ubicación, sensor de luz y Geocoder.
- Los textos visibles están centralizados en `strings.xml`.

## 1:10–2:35 — Cámara y galería

1. Abre Cámara y galería.
2. En Foto, toma una fotografía y muestra el resultado dentro del contenedor fijo.
3. Selecciona una fotografía desde la galería.
4. Cambia el selector a Video, graba un clip corto y reprodúcelo.
5. Selecciona un video desde la galería.

Explica: “Usamos Activity Result para solicitar el permiso de cámara, abrir la aplicación de cámara y recibir el resultado. Las capturas usan una URI segura de FileProvider. Para la galería usamos Photo Picker, por lo que no solicitamos acceso general al almacenamiento.”

## 2:35–4:45 — Mapa y localización

1. Abre el mapa y acepta el permiso de ubicación.
2. Muestra el marcador de la ubicación actual.
3. Desplázate o simula varias ubicaciones para mostrar la `Polyline`.
4. Cubre el sensor de luz para activar el estilo oscuro y descúbrelo para volver al claro.
5. Busca “Universidad Javeriana, Bogotá”; muestra el marcador y el movimiento de cámara.
6. Mantén pulsado otro punto, toca el marcador y muestra la dirección obtenida.

Explica: “Fused Location Provider entrega las posiciones y el ViewModel conserva la ruta. El sensor `TYPE_LIGHT` decide el estilo con un umbral de 50 lux. Geocoder convierte texto en coordenadas y también coordenadas en una dirección legible.”

## 4:45–5:30 — Código clave

Muestra brevemente:

- `MapScreen.kt`: mapa, permisos, marcadores, `Polyline` y acciones del usuario.
- `MapViewModel.kt`: estado de ubicación, recorrido, búsqueda y mensajes.
- `GeocodingService.kt`: geocodificación directa e inversa compatible con versiones nuevas y antiguas de Android.
- `LightSensorObserver.kt` y `LocationTracker.kt`: registro y liberación de sensores y ubicación según el ciclo de vida.

## 5:30–5:50 — Cierre

“Con esto cubrimos los siete criterios funcionales de la rúbrica: dos de multimedia y cinco de mapas. También cumplimos Single Activity, Navigation 3, Ktor 3.5.2, temas claro y oscuro y textos centralizados.”
