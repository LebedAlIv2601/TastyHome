package com.lebedaliv2601.base.platform.camera

import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import com.lebedaliv2601.base.foundation.date.DateUtils
import com.lebedaliv2601.base.platform.file.File
import com.lebedaliv2601.base.platform.file.FileManager
import com.lebedaliv2601.base.platform.file.image.Image
import com.lebedaliv2601.base.platform.permissions.Permission
import com.lebedaliv2601.base.platform.permissions.PermissionManager
import com.lebedaliv2601.base.platform.permissions.PermissionStatus
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSURL
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraCaptureMode
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import kotlin.coroutines.resume

private const val COMPRESSION_QUALITY = 0.9

@OptIn(ExperimentalForeignApi::class)
internal class IosCameraManager(
    private val viewController: UIViewController,
    private val permissionManager: PermissionManager,
    private val fileManager: FileManager
) : CameraManager {

    override suspend fun takePhoto(): Image? {
        if (permissionManager.requestPermission(Permission.CAMERA) != PermissionStatus.GRANTED) {
            return null
        }
        val photoFile = fileManager.createTempFile("photo_${DateUtils.currentTimeMillis()}.jpg")
        val file = withContext(MyDispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val picker = UIImagePickerController().apply {
                    setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
                    setCameraCaptureMode(
                        UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto
                    )
                    delegate = object :
                        NSObject(),
                        UIImagePickerControllerDelegateProtocol,
                        UINavigationControllerDelegateProtocol {
                        override fun imagePickerController(
                            picker: UIImagePickerController,
                            didFinishPickingMediaWithInfo: Map<Any?, *>
                        ) {
                            picker.dismissViewControllerAnimated(true) {}
                            val uiImage = (
                                didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage]
                                    ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage]
                                ) as? UIImage
                            val written = uiImage?.let { writeImageToFile(it, photoFile) } == true
                            cont.resume(if (written) photoFile else null)
                        }

                        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                            picker.dismissViewControllerAnimated(true) {}
                            cont.resume(null)
                        }
                    }
                }
                viewController.presentViewController(picker, animated = true) {}
                cont.invokeOnCancellation {
                    picker.dismissViewControllerAnimated(false) {}
                }
            }
        }
        return file?.let { fileManager.loadImage(it) }
    }

    private fun writeImageToFile(uiImage: UIImage, file: File): Boolean {
        val data = UIImageJPEGRepresentation(uiImage, COMPRESSION_QUALITY) ?: return false
        val url = NSURL.fileURLWithPath(file.getPath())
        return data.writeToURL(url, atomically = true)
    }
}
