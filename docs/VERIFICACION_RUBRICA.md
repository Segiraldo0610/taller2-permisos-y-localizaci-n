# Verificación contra la rúbrica del Taller 2

Revisión actualizada el 16 de octubre de 2026 con las instrucciones publicadas en el Campus Virtual de la Pontificia Universidad Javeriana. La tabla distingue entre una funcionalidad implementada en código y una funcionalidad ya comprobada manualmente en un dispositivo. Una casilla implementada no reemplaza la prueba final.

## Criterios funcionales

| Criterio del Campus | Valor | Responsable | Estado del código | Evidencia | Validación pendiente |
|---|---:|---|---|---|---|
| Pantalla principal con dos botones, iconos o imágenes | Requisito base | Sebastián | Implementado | `ui/home/HomeScreen.kt` | Comprobar navegación y botón Atrás |
| Pantalla multimedia con selector Foto/Video y vista previa centrada de tamaño fijo | Requisito base | Sebastián | Implementado | `ui/media/MediaScreen.kt` | Revisar proporción en el dispositivo de demostración |
| Tomar foto y grabar video solicitando permiso de cámara; mostrar el resultado en la app | 25 % | Sebastián | Implementado | `MediaScreen.kt`, `MediaFileProvider.kt`, `AndroidManifest.xml` | Probar foto, video, cancelación y permiso rechazado |
| Seleccionar foto y video desde la galería; mostrar el resultado en la app | 25 % | Sebastián | Implementado | `PickVisualMedia` en `MediaScreen.kt` | Probar ambos tipos con archivos reales |
| Mapa con marcador en la ubicación actual | 10 % | Samuel | Implementado | `MapScreen.kt`, `LocationTracker.kt` y permisos del manifiesto | Probar con ubicación precisa, aproximada y permiso rechazado |
| Estilo claro/oscuro del mapa según el sensor de luminosidad | 10 % | Samuel | Implementado | `LightSensorObserver.kt`, `themeModeForLux` y `map_style_dark.json` | Probar cubriendo y descubriendo el sensor del dispositivo |
| `Polyline` actualizada cuando cambia la ubicación | 10 % | Samuel | Implementado | `appendRoutePoint`, `MapViewModel.onLocation` y `Polyline` en `MapScreen.kt` | Recorrer una ruta real o simularla desde Extended controls |
| Dirección escrita → `Geocoder` → marcador y cámara en el resultado | 10 % | Samuel | Implementado | `GeocodingService.search` y campo de búsqueda de `MapScreen.kt` | Buscar “Universidad Javeriana, Bogotá” |
| LongClick → geocodificación inversa → marcador con dirección como título | 10 % | Samuel | Implementado | `GoogleMap.onMapLongClick` y `GeocodingService.reverse` | Mantener pulsado un punto y abrir el marcador |

El frente multimedia de Sebastián y el frente de mapas de Samuel cubren el 100 % funcional de la rúbrica en código. La entrega solo se considera terminada después de ejecutar las pruebas manuales señaladas, generar el APK final y grabar el video.

## Requisitos técnicos obligatorios

| Instrucción del Campus | Estado | Evidencia |
|---|---|---|
| Kotlin y Jetpack Compose | Cumple | Configuración de `app/build.gradle.kts` y pantallas Compose |
| Arquitectura Single Activity | Cumple | El manifiesto declara solamente `MainActivity` |
| `MainActivity.kt` sin declaraciones `@Composable` | Cumple | La actividad se limita a `setContent` |
| Textos visibles en `strings.xml` | Cumple | `res/values/strings.xml`; los composables usan `stringResource` |
| Navigation 3 versión 1.1.6 o superior | Cumple | Navigation 3 `1.1.7`, `rememberNavBackStack` y `NavDisplay` |
| Ktor versión 3.5.2 o superior | Cumple | Ktor `3.5.2` y `network/HttpClientProvider.kt` |
| Tema claro y oscuro | Cumple | `ui/theme/Theme.kt` y recursos `values`/`values-night` |
| Clave de Google Maps fuera del repositorio | Cumple | `local.properties`, `manifestPlaceholders` y metadato `com.google.android.geo.API_KEY` |
| Composables reutilizables sugeridos | Cumple | `DestinationCard`, `MediaModeSelector`, `MediaPreview`, `MediaMessage` y componentes de `MapScreen` |

## Permisos y comportamiento esperado

- La cámara se solicita solo al pulsar **Tomar foto** o **Grabar video**.
- Una negación mantiene la pantalla utilizable y permite volver a intentarlo.
- Las capturas usan la aplicación de cámara mediante Activity Result y una URI de `FileProvider`.
- Si la captura se cancela, se elimina el archivo temporal.
- Photo Picker filtra `ImageOnly` o `VideoOnly` según el Switch; no necesita permiso general de almacenamiento.
- La parte de mapas debe solicitar siempre `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION` juntas. Si Android concede solo ubicación aproximada, la aplicación debe manejar ese resultado sin cambiar la solicitud original.
- Los observadores de ubicación y luminosidad se activan con `ON_START` y se liberan con `ON_STOP` para evitar actualizaciones fuera de pantalla.
- Si el dispositivo no tiene sensor de luz, el estilo del mapa usa el tema claro u oscuro del sistema.

## Pruebas del frente de mapas

| Caso | Pasos | Resultado esperado | Estado |
|---|---|---|---|
| Permiso de ubicación | Abrir Mapa y conceder ubicación precisa o aproximada | El mapa sigue disponible y muestra la posición cuando el proveedor entrega una lectura | Pendiente en dispositivo |
| Permiso rechazado | Abrir Mapa, negar ubicación y pulsar Permitir | La pantalla no se cierra y permite volver a solicitar el permiso | Pendiente en dispositivo |
| Cambio por luminosidad | Cubrir y descubrir el sensor de luz | El mapa cambia entre el estilo oscuro y el estilo claro | Pendiente en dispositivo físico |
| Recorrido | Caminar o enviar varias ubicaciones desde el emulador | Aparece una línea que une las posiciones recibidas | Pendiente en dispositivo/emulador |
| Geocodificación directa | Buscar “Universidad Javeriana, Bogotá” | Aparece un marcador y la cámara se centra en el resultado | Pendiente en dispositivo |
| Geocodificación inversa | Mantener pulsado un punto del mapa | Aparece un marcador cuyo título contiene la dirección | Pendiente en dispositivo |

## Pruebas del frente multimedia

| Caso | Pasos | Resultado esperado | Estado |
|---|---|---|---|
| Permiso rechazado | Abrir Multimedia, pulsar Tomar foto y negar Cámara | Mensaje comprensible y aplicación estable | Pendiente en dispositivo |
| Foto de cámara | Elegir Foto, pulsar Tomar foto y aceptar la captura | Fotografía visible en el contenedor fijo; origen Cámara | Pendiente en dispositivo |
| Video de cámara | Elegir Video, pulsar Grabar video y aceptar | Video reproducible en el mismo contenedor; origen Cámara | Pendiente en dispositivo |
| Foto de galería | Elegir Foto y pulsar Seleccionar foto | Solo permite elegir imagen y la muestra; origen Galería | Pendiente en dispositivo |
| Video de galería | Elegir Video y pulsar Seleccionar video | Solo permite elegir video y lo reproduce; origen Galería | Pendiente en dispositivo |
| Cancelación | Cancelar cámara y Photo Picker | Mensaje breve, sin cierre inesperado | Pendiente en dispositivo |
| Cambio de modo | Mostrar una foto y cambiar a Video | Limpia el resultado incompatible y actualiza los botones | Pendiente en dispositivo |
| Rotación/retorno | Rotar o volver desde la aplicación externa | No se pierde la URI pendiente durante el cambio de configuración | Pendiente en dispositivo |

## Entregables exigidos

- [ ] Código fuente comprimido en ZIP o RAR desde el commit final.
- [ ] El proyecto genera `app/build/outputs/apk/debug/app-debug.apk` en el commit final.
- [ ] APK final regenerado después de integrar la rama de mapas.
- [ ] Video de máximo seis minutos cargado en YouTube con acceso no privado.
- [ ] El video explica paquetes, composables, pantallas, cámara mediante Intent/Activity Result y `Geocoder`.
- [ ] Enlace de YouTube y archivos finales revisados por Sebastián y Samuel antes del último envío.

## Comandos de control

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:lintDebug
.\gradlew.bat :app:testDebugUnitTest
```

La compilación y Lint verifican estructura y código, pero las rutas de cámara, galería, ubicación, sensor y mapa deben validarse manualmente en el dispositivo o emulador usado para la demostración.
