# Prueba de recorrido sin celular

`ruta_simulada_javeriana.gpx` contiene 12 ubicaciones ficticias cercanas a la Universidad Javeriana para probar el marcador y la `Polyline` en el emulador. No es una ruta real.

1. Inicia un emulador con Google Play y abre la pantalla Mapa de la aplicación. Concede el permiso de ubicación. Para ver el mapa se necesita una clave válida de Maps SDK for Android en `local.properties` (`MAPS_API_KEY=...`); nunca subas esa clave al repositorio.
2. Opción visual: en los controles extendidos del emulador (`...`), entra a **Location > Routes**, importa el archivo GPX y ejecuta la ruta.
3. Opción PowerShell: con `adb` disponible, ejecuta desde esta carpeta `./reproducir_ruta.ps1`. Si el emulador usa otro identificador, consulta `adb devices` y pasa `-Serial <identificador>`. Para cambiar la pausa entre puntos, usa `-IntervalSeconds 6` (o un valor de 1 a 60).
4. Comprueba en la app que el marcador cambia de posición y que la línea une los puntos. Enviar ubicaciones al emulador, por sí solo, no confirma que la app las haya dibujado.

Estos recursos son solo para pruebas manuales; no modifican el comportamiento de la app ni sustituyen la validación en pantalla.
