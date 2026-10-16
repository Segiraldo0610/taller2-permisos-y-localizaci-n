package com.sebastianysamuel.taller2.ui.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

data class CaptureTarget(
    val uri: Uri,
    val absolutePath: String,
)

fun createCaptureTarget(
    context: Context,
    mode: MediaMode,
): CaptureTarget {
    val captureDirectory = File(context.cacheDir, "captured_media").apply { mkdirs() }
    val extension = if (mode == MediaMode.PHOTO) ".jpg" else ".mp4"
    val prefix = if (mode == MediaMode.PHOTO) "photo_" else "video_"
    val outputFile = File.createTempFile(prefix, extension, captureDirectory)

    return CaptureTarget(
        uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outputFile,
        ),
        absolutePath = outputFile.absolutePath,
    )
}

fun deleteCancelledCapture(absolutePath: String?) {
    absolutePath?.let(::File)?.takeIf(File::exists)?.delete()
}
