package com.tastyhome.base.platform.picker

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.platform.ActivityHolder
import com.tastyhome.base.platform.file.File
import com.tastyhome.base.platform.file.FileManager
import com.tastyhome.base.platform.permissions.Permission
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.permissions.PermissionStatus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

private const val PICK_MEDIA_LAUNCHER_KEY = "pick_visual_media"
private const val PICK_FILE_LAUNCHER_KEY = "pick_file"

private const val MAX_ITEMS = 100

internal class AndroidFilePicker(
    private val activityHolder: ActivityHolder,
    private val context: Context,
    private val permissionManager: PermissionManager,
    private val fileManager: FileManager,
) : FilePicker, ActivityHolder.Callback {

    init {
        activityHolder.addCallback(this)
    }

    private var mediaContinuation: Continuation<List<Uri>>? = null
    private var fileContinuation: Continuation<List<Uri>>? = null

    private var pickMediaSingleLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var pickMediaMultipleLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var pickFileLauncher: ActivityResultLauncher<String>? = null

    override fun onChange(activity: Activity?) {
        pickMediaSingleLauncher = null
        pickMediaMultipleLauncher = null
        pickFileLauncher = null
        if (activity is ComponentActivity) {
            pickMediaSingleLauncher = activity.activityResultRegistry.register(
                "${PICK_MEDIA_LAUNCHER_KEY}_single",
                activity,
                ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                mediaContinuation?.resume(uri?.let { listOf(it) } ?: emptyList())
                mediaContinuation = null
            }
            pickMediaMultipleLauncher = activity.activityResultRegistry.register(
                PICK_MEDIA_LAUNCHER_KEY,
                activity,
                ActivityResultContracts.PickMultipleVisualMedia(MAX_ITEMS)
            ) { uris ->
                mediaContinuation?.resume(uris)
                mediaContinuation = null
            }
            pickFileLauncher = activity.activityResultRegistry.register(
                PICK_FILE_LAUNCHER_KEY,
                activity,
                ActivityResultContracts.GetMultipleContents()
            ) { uris ->
                fileContinuation?.resume(uris)
                fileContinuation = null
            }
        }
    }

    override suspend fun pickMedia(
        allowMultiple: Boolean,
        mediaType: MediaType
    ): List<File> {
        if (permissionManager.requestPermission(Permission.MEDIA) != PermissionStatus.GRANTED) {
            return emptyList()
        }
        val mediaTypeFilter = when (mediaType) {
            MediaType.IMAGE -> ActivityResultContracts.PickVisualMedia.ImageOnly
            MediaType.VIDEO -> ActivityResultContracts.PickVisualMedia.VideoOnly
            MediaType.ALL -> ActivityResultContracts.PickVisualMedia.ImageAndVideo
        }
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(mediaTypeFilter)
            .build()
        val launcher = if (allowMultiple) pickMediaMultipleLauncher else pickMediaSingleLauncher
        if (launcher == null) return emptyList()
        val uris = withContext(MyDispatchers.MainImmediate) {
            suspendCancellableCoroutine<List<Uri>> { cont ->
                mediaContinuation = cont
                launcher.launch(request)
                cont.invokeOnCancellation { mediaContinuation = null }
            }
        }
        return withContext(MyDispatchers.IO) {
            uris.mapNotNull { createFileFromUri(it) }
        }
    }

    override suspend fun pickFile(
        allowMultiple: Boolean,
        mimeTypes: List<String>
    ): List<File> {
        val launcher = pickFileLauncher ?: return emptyList()
        val mimeType = mimeTypes.firstOrNull() ?: "*/*"
        val uris = withContext(MyDispatchers.MainImmediate) {
            suspendCancellableCoroutine { cont ->
                fileContinuation = cont
                launcher.launch(mimeType)
                cont.invokeOnCancellation { fileContinuation = null }
            }
        }
        val list = withContext(MyDispatchers.IO) {
            uris.mapNotNull { createFileFromUri(it) }
        }
        return if (!allowMultiple) list.take(1) else list
    }

    private suspend fun createFileFromUri(uri: Uri): File? {
        return try {
            val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
            } ?: "picked_${System.currentTimeMillis()}.${getExtensionFromMime(context.contentResolver.getType(uri))}"
            val tempFile = fileManager.createTempFile(name)
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
            if (fileManager.writeFileBytes(tempFile, bytes)) tempFile else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getExtensionFromMime(mime: String?): String = when (mime?.substringBefore("/")) {
        "image" -> when (mime) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            else -> "jpg"
        }
        "video" -> "mp4"
        "application" -> when (mime) {
            "application/pdf" -> "pdf"
            else -> "bin"
        }
        else -> "bin"
    }
}
