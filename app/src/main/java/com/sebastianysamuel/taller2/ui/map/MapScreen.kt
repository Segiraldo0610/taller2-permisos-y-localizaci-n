package com.sebastianysamuel.taller2.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.sebastianysamuel.taller2.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = viewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val systemDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val state = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val locationTracker = remember(context) { LocationTracker(context) }
    val lightSensorObserver = remember(context) { LightSensorObserver(context) }
    val geocodingService = remember(context) { GeocodingService(context) }
    var hasLocationPermission by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    var centeredOnUser by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) viewModel.onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(LOCATION_PERMISSIONS)
        }
    }

    DisposableEffect(lifecycleOwner, hasLocationPermission, systemDark) {
        fun startObservers() {
            val sensorAvailable = lightSensorObserver.start { lux ->
                viewModel.onLux(lux, systemDark)
            }
            if (!sensorAvailable) viewModel.onLux(null, systemDark)

            if (hasLocationPermission) {
                locationTracker.start(
                    onLocation = viewModel::onLocation,
                    onUnavailable = viewModel::onLocationUnavailable,
                )
            }
        }

        fun stopObservers() {
            locationTracker.stop()
            lightSensorObserver.stop()
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> startObservers()
                Lifecycle.Event.ON_STOP -> stopObservers()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            startObservers()
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stopObservers()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM)
    }

    LaunchedEffect(state.currentLocation) {
        val point = state.currentLocation
        if (!centeredOnUser && point != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(point.toLatLng(), USER_ZOOM),
            )
            centeredOnUser = true
        }
    }

    LaunchedEffect(state.searchMarker) {
        state.searchMarker?.let { marker ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(marker.point.toLatLng(), SEARCH_ZOOM),
            )
        }
    }

    val message = state.message?.let { mapMessage ->
        stringResource(mapMessage.stringResource())
    }
    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    fun searchAddress() {
        val query = viewModel.beginSearch() ?: return
        keyboardController?.hide()
        scope.launch {
            runCatching { geocodingService.search(query) }
                .onSuccess(viewModel::onSearchResult)
                .onFailure { viewModel.onGeocodingUnavailable() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val darkMapStyle = remember(context) {
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,
                    mapStyleOptions = if (state.themeMode == MapThemeMode.DARK) {
                        darkMapStyle
                    } else {
                        null
                    },
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = hasLocationPermission,
                    zoomControlsEnabled = false,
                ),
                onMapLongClick = { latLng ->
                    val point = RoutePoint(latLng.latitude, latLng.longitude)
                    scope.launch {
                        runCatching { geocodingService.reverse(point) }
                            .onSuccess { address -> viewModel.onSelectedAddress(point, address) }
                            .onFailure { viewModel.onGeocodingUnavailable() }
                    }
                },
            ) {
                state.currentLocation?.let { point ->
                    Marker(
                        state = MarkerState(point.toLatLng()),
                        title = stringResource(R.string.current_location),
                    )
                }
                state.searchMarker?.let { marker ->
                    Marker(
                        state = MarkerState(marker.point.toLatLng()),
                        title = marker.title,
                    )
                }
                state.selectedMarker?.let { marker ->
                    Marker(
                        state = MarkerState(marker.point.toLatLng()),
                        title = marker.title.ifBlank {
                            stringResource(R.string.selected_location)
                        },
                    )
                }
                if (state.route.size > 1) {
                    Polyline(
                        points = state.route.map(RoutePoint::toLatLng),
                        color = MaterialTheme.colorScheme.primary,
                        width = ROUTE_WIDTH,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 4.dp,
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text(stringResource(R.string.address_search_label)) },
                        placeholder = { Text(stringResource(R.string.address_search_example)) },
                        singleLine = true,
                        enabled = !state.isSearching,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { searchAddress() }),
                        trailingIcon = {
                            if (state.isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                IconButton(onClick = ::searchAddress) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = stringResource(R.string.search_address),
                                    )
                                }
                            }
                        },
                    )
                }

                if (!hasLocationPermission) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.location_permission_required),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Button(
                                onClick = { permissionLauncher.launch(LOCATION_PERMISSIONS) },
                                modifier = Modifier.padding(start = 8.dp),
                            ) {
                                Text(stringResource(R.string.grant_permission))
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(50),
                tonalElevation = 6.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.MyLocation, contentDescription = null)
                    Text(
                        text = state.lux?.let {
                            stringResource(R.string.map_lux_value, it)
                        } ?: stringResource(R.string.map_lux_unavailable),
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

private fun android.content.Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

private fun RoutePoint.toLatLng(): LatLng = LatLng(latitude, longitude)

private fun MapMessage.stringResource(): Int = when (this) {
    MapMessage.LOCATION_PERMISSION_DENIED -> R.string.location_permission_denied
    MapMessage.LOCATION_UNAVAILABLE -> R.string.location_unavailable
    MapMessage.ADDRESS_NOT_FOUND -> R.string.address_not_found
    MapMessage.GEOCODING_UNAVAILABLE -> R.string.geocoding_unavailable
}

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

private val DEFAULT_LOCATION = LatLng(4.6285, -74.0649)
private const val DEFAULT_ZOOM = 12f
private const val USER_ZOOM = 17f
private const val SEARCH_ZOOM = 16f
private const val ROUTE_WIDTH = 10f
