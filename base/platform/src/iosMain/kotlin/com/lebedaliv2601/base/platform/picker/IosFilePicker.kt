package com.lebedaliv2601.base.platform.picker

import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import com.lebedaliv2601.base.foundation.date.DateUtils
import com.lebedaliv2601.base.platform.file.File
import com.lebedaliv2601.base.platform.permissions.Permission
import com.lebedaliv2601.base.platform.permissions.PermissionManager
import com.lebedaliv2601.base.platform.permissions.PermissionStatus
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.writeToURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import kotlin.coroutines.resume

private const val PUBLIC_DATA = "public.data"
private const val QUALITY = 0.9

@OptIn(ExperimentalForeignApi::class)
internal class IosFilePicker(
    private val viewController: UIViewController,
    private val permissionManager: PermissionManager,
) : FilePicker {

    override suspend fun pickMedia(
        allowMultiple: Boolean,
        mediaType: MediaType
    ): List<File> {
        if (permissionManager.requestPermission(Permission.MEDIA) != PermissionStatus.GRANTED) {
            return emptyList()
        }
        if (mediaType == MediaType.VIDEO) {
            return emptyList()
        }
        val file = withContext(MyDispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val picker = UIImagePickerController().apply {
                    setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary)
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
                            val fileUrl = uiImage?.let { saveImageToTempFile(it) }
                            cont.resume(fileUrl?.let { File(it) })
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
        return file?.let { listOf(it) } ?: emptyList()
    }

    override suspend fun pickFile(
        allowMultiple: Boolean,
        mimeTypes: List<String>
    ): List<File> {
        val documentTypes = mimeTypes.map { mimeToUti(it) }.ifEmpty { listOf(PUBLIC_DATA) }
        return withContext(MyDispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val picker = UIDocumentPickerViewController(
                    documentTypes = documentTypes,
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
                ).apply {
                    allowsMultipleSelection = allowMultiple
                    delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                        override fun documentPicker(
                            controller: UIDocumentPickerViewController,
                            didPickDocumentsAtURLs: List<*>
                        ) {
                            controller.dismissViewControllerAnimated(true) {}
                            val files = didPickDocumentsAtURLs.mapNotNull { (it as? NSURL)?.let { url -> File(url) } }
                            cont.resume(files)
                        }

                        override fun documentPickerWasCancelled(
                            controller: UIDocumentPickerViewController
                        ) {
                            controller.dismissViewControllerAnimated(true) {}
                            cont.resume(emptyList())
                        }
                    }
                }
                viewController.presentViewController(picker, animated = true) {}
                cont.invokeOnCancellation {
                    picker.dismissViewControllerAnimated(false) {}
                }
            }
        }
    }

    private fun saveImageToTempFile(uiImage: UIImage): NSURL? {
        val data = UIImageJPEGRepresentation(uiImage, QUALITY) ?: return null
        val path = "${NSTemporaryDirectory()}picker_${DateUtils.currentTimeMillis()}.jpg"
        val url = NSURL.fileURLWithPath(path)
        return if (data.writeToURL(url, atomically = true)) url else null
    }

    private fun mimeToUti(mime: String): String = when {
        mime == "*/*" -> PUBLIC_DATA
        mime.startsWith("image/") -> "public.image"
        mime.startsWith("video/") -> "public.movie"
        mime == "application/pdf" -> "com.adobe.pdf"
        mime.startsWith("application/") -> PUBLIC_DATA
        mime.startsWith("text/") -> "public.plain-text"
        else -> PUBLIC_DATA
    }
}
