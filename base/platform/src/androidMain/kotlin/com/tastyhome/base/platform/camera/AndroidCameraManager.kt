package com.tastyhome.base.platform.camera

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.platform.ActivityHolder
import com.tastyhome.base.platform.file.FileManager
import com.tastyhome.base.platform.file.image.Image
import com.tastyhome.base.platform.permissions.Permission
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.permissions.PermissionStatus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File as JavaFile
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

private const val TAKE_PICTURE_LAUNCHER_KEY = "kmp_take_picture"

internal class AndroidCameraManager(
    private val activityHolder: ActivityHolder,
    private val context: Context,
    private val permissionManager: PermissionManager,
    private val fileManager: FileManager,
) : CameraManager, ActivityHolder.Callback {

    init {
        activityHolder.addCallback(this)
    }

    private var currentContinuation: Continuation<Boolean>? = null
    private var currentPhotoFile: JavaFile? = null
    private var takePictureLauncher: ActivityResultLauncher<Uri>? = null

    override fun onChange(activity: Activity?) {
        takePictureLauncher = null
        if (activity is ComponentActivity) {
            takePictureLauncher = activity.activityResultRegistry.register(
                TAKE_PICTURE_LAUNCHER_KEY,
                activity,
                ActivityResultContracts.TakePicture()
            ) { success ->
                currentContinuation?.resume(success)
                currentContinuation = null
                currentPhotoFile = null
            }
        }
    }

    override suspend fun takePhoto(): Image? {
        if (permissionManager.requestPermission(Permission.CAMERA) != PermissionStatus.GRANTED) {
            return null
        }
        val launcher = takePictureLauncher ?: return null
        val photoFile = fileManager.createImageFile("photo_${System.currentTimeMillis()}.jpg")
        val javaFile = photoFile.file
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            javaFile
        )
        val success = withContext(MyDispatchers.MainImmediate) {
            suspendCancellableCoroutine { cont ->
                currentContinuation = cont
                currentPhotoFile = javaFile
                launcher.launch(uri)
            }
        }
        return if (success && photoFile.exists()) {
            fileManager.loadImage(photoFile)
        } else {
            null
        }
    }
}
