package com.sebastianysamuel.taller2.ui.media

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class MediaMode {
    PHOTO,
    VIDEO,
}

enum class MediaSource {
    CAMERA,
    GALLERY,
}

enum class MediaMessage {
    CAMERA_PERMISSION_DENIED,
    CAPTURE_CANCELLED,
    SELECTION_CANCELLED,
    CAMERA_UNAVAILABLE,
}

data class MediaUiState(
    val mode: MediaMode = MediaMode.PHOTO,
    val mediaUri: Uri? = null,
    val mediaSource: MediaSource? = null,
    val message: MediaMessage? = null,
)

class MediaViewModel : ViewModel() {
    var uiState by mutableStateOf(MediaUiState())
        private set

    fun selectMode(mode: MediaMode) {
        if (mode != uiState.mode) {
            uiState = uiState.copy(
                mode = mode,
                mediaUri = null,
                mediaSource = null,
                message = null,
            )
        }
    }

    fun showMedia(uri: Uri, source: MediaSource) {
        uiState = uiState.copy(
            mediaUri = uri,
            mediaSource = source,
            message = null,
        )
    }

    fun showMessage(message: MediaMessage) {
        uiState = uiState.copy(message = message)
    }

    fun dismissMessage() {
        uiState = uiState.copy(message = null)
    }
}
