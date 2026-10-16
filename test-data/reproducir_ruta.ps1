param(
    [string]$AdbPath = "adb",
    [string]$Serial = "emulator-5554",
    [string]$GpxPath = (Join-Path $PSScriptRoot "ruta_simulada_javeriana.gpx"),
    [ValidateRange(1, 60)]
    [int]$IntervalSeconds = 6
)

if (-not (Test-Path -LiteralPath $GpxPath)) {
    throw "No se encontro la ruta GPX: $GpxPath"
}

[xml]$routeDocument = Get-Content -Raw -LiteralPath $GpxPath
$points = @($routeDocument.gpx.trk.trkseg.trkpt)
if ($points.Count -lt 2) {
    throw "La ruta debe tener al menos dos puntos."
}

Write-Output "Enviando $($points.Count) ubicaciones a $Serial. Abre la pantalla Mapa y concede ubicacion antes de iniciar."
for ($index = 0; $index -lt $points.Count; $index++) {
    $point = $points[$index]
    $longitude = [string]$point.lon
    $latitude = [string]$point.lat
    & $AdbPath -s $Serial emu geo fix $longitude $latitude
    if ($LASTEXITCODE -ne 0) {
        throw "ADB no pudo enviar el punto $($index + 1)."
    }
    Write-Output "Punto $($index + 1)/$($points.Count): lat=$latitude, lon=$longitude"
    if ($index -lt $points.Count - 1) {
        Start-Sleep -Seconds $IntervalSeconds
    }
}

Write-Output "Ruta enviada. Comprueba que el marcador se desplazo y que aparecio la Polyline."
