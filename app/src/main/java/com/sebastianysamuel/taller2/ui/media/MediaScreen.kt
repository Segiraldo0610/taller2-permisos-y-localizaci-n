package com.sebastianysamuel.taller2.ui.media

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.sebastianysamuel.taller2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    onBack: () -> Unit,
    viewModel: MediaViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state = viewModel.uiState
    var pendingUri by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingPath by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingCaptureMode by rememberSaveable { mutableStateOf<String?>(null) }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        val uri = pendingUri?.toUri()
        if (success && uri != null) {
            viewModel.showMedia(uri, MediaSource.CAMERA)
        } else {
            deleteCancelledCapture(pendingPath)
            viewModel.showMessage(MediaMessage.CAPTURE_CANCELLED)
        }
        pendingUri = null
        pendingPath = null
    }

    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
    ) { success ->
        val uri = pendingUri?.toUri()
        if (success && uri != null) {
            viewModel.showMedia(uri, MediaSource.CAMERA)
        } else {
            deleteCancelledCapture(pendingPath)
            viewModel.showMessage(MediaMessage.CAPTURE_CANCELLED)
        }
        pendingUri = null
        pendingPath = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val mode = pendingCaptureMode?.let(MediaMode::valueOf)
        pendingCaptureMode = null
        if (granted && mode != null) {
            runCatching {
                val target = createCaptureTarget(context, mode)
                pendingUri = target.uri.toString()
                pendingPath = target.absolutePath
                if (mode == MediaMode.PHOTO) {
                    takePhotoLauncher.launch(target.uri)
                } else {
                    captureVideoLauncher.launch(target.uri)
                }
            }.onFailure {
                deleteCancelledCapture(pendingPath)
                pendingUri = null
                pendingPath = null
                viewModel.showMessage(MediaMessage.CAMERA_UNAVAILABLE)
            }
        } else {
            viewModel.showMessage(MediaMessage.CAMERA_PERMISSION_DENIED)
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            viewModel.showMedia(uri, MediaSource.GALLERY)
        } else {
            viewModel.showMessage(MediaMessage.SELECTION_CANCELLED)
        }
    }

    fun launchCapture(mode: MediaMode) {
        runCatching {
            val target = createCaptureTarget(context, mode)
            pendingUri = target.uri.toString()
            pendingPath = target.absolutePath
            if (mode == MediaMode.PHOTO) {
                takePhotoLauncher.launch(target.uri)
            } else {
                captureVideoLauncher.launch(target.uri)
            }
        }.onFailure {
            deleteCancelledCapture(pendingPath)
            pendingUri = null
            pendingPath = null
            viewModel.showMessage(MediaMessage.CAMERA_UNAVAILABLE)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.media_title)) },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MediaModeSelector(
                selectedMode = state.mode,
                onModeSelected = viewModel::selectMode,
            )
            Spacer(Modifier.height(20.dp))
            MediaPreview(
                mode = state.mode,
                uri = state.mediaUri,
            )
            state.mediaSource?.let { source ->
                Text(
                    text = stringResource(
                        if (source == MediaSource.CAMERA) {
                            R.string.media_source_camera
                        } else {
                            R.string.media_source_gallery
                        },
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA,
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        launchCapture(state.mode)
                    } else {
                        pendingCaptureMode = state.mode.name
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = if (state.mode == MediaMode.PHOTO) {
                        Icons.Rounded.PhotoCamera
                    } else {
                        Icons.Rounded.Videocam
                    },
                    contentDescription = null,
                )
                Text(
                    text = stringResource(
                        if (state.mode == MediaMode.PHOTO) {
                            R.string.take_photo
                        } else {
                            R.string.record_video
                        },
                    ),
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(
                            if (state.mode == MediaMode.PHOTO) {
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            } else {
                                ActivityResultContracts.PickVisualMedia.VideoOnly
                            },
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.Collections, contentDescription = null)
                Text(
                    text = stringResource(
                        if (state.mode == MediaMode.PHOTO) {
                            R.string.select_photo
                        } else {
                            R.string.select_video
                        },
                    ),
                    modifier = Modifier.padding(start = 10.dp),
                )
            }

            state.message?.let { message ->
                MediaMessage(
                    message = message,
                    onDismiss = viewModel::dismissMessage,
                )
            }
        }
    }
}

@Composable
private fun MediaModeSelector(
    selectedMode: MediaMode,
    onModeSelected: (MediaMode) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.media_mode),
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.PhotoCamera, contentDescription = null)
            Text(stringResource(R.string.photo))
            Switch(
                checked = selectedMode == MediaMode.VIDEO,
                onCheckedChange = { videoSelected ->
                    onModeSelected(
                        if (videoSelected) MediaMode.VIDEO else MediaMode.PHOTO,
                    )
                },
            )
            Text(stringResource(R.string.video))
            Icon(Icons.Rounded.Videocam, contentDescription = null)
        }
    }
}

@Composable
private fun MediaPreview(
    mode: MediaMode,
    uri: Uri?,
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 300.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uri == null -> Text(
                text = stringResource(R.string.no_media),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(28.dp),
            )

            mode == MediaMode.PHOTO -> AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(uri)
                    .build(),
                contentDescription = stringResource(R.string.media_preview),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            else -> key(uri) {
                AndroidView(
                    factory = { videoContext ->
                        VideoView(videoContext).apply {
                            setMediaController(MediaController(videoContext))
                            setVideoURI(uri)
                            setOnPreparedListener { player ->
                                player.isLooping = true
                                start()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    onRelease = VideoView::stopPlayback,
                )
            }
        }
    }
}

@Composable
private fun MediaMessage(
    message: MediaMessage,
    onDismiss: () -> Unit,
) {
    val messageResource = when (message) {
        MediaMessage.CAMERA_PERMISSION_DENIED -> R.string.camera_permission_denied
        MediaMessage.CAPTURE_CANCELLED -> R.string.capture_cancelled
        MediaMessage.SELECTION_CANCELLED -> R.string.selection_cancelled
        MediaMessage.CAMERA_UNAVAILABLE -> R.string.camera_unavailable
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(messageResource),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
        )
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.dismiss_message))
        }
    }
}
