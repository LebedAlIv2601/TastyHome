@file:Suppress("detekt:StringLiteralDuplication")

package com.tastyhome.base.platform.file

import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.foundation.date.DateUtils
import com.tastyhome.base.logger.L
import com.tastyhome.base.platform.file.image.ImageFormat
import com.tastyhome.base.platform.file.image.ImagePreview
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImageWriteToSavedPhotosAlbum
import platform.posix.memcpy
import com.tastyhome.base.platform.file.image.Image

private const val QUALITY = 90
private const val COMPRESSION_QUALITY = 0.9

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class IosFileManager : FileManager {

    private val fileManager = NSFileManager.defaultManager

    override suspend fun createFile(fileName: String?): File = withContext(MyDispatchers.IO) {
        val name = fileName ?: "${DateUtils.currentTimeMillis()}.pdf"
        val path = "${getCacheDirectory()}/$name"
        val url = NSURL.fileURLWithPath(path)
        NSData.create(bytes = null, length = 0u).writeToURL(url, atomically = true)
        File(url)
    }

    override suspend fun createImageFile(fileName: String?): File = withContext(MyDispatchers.IO) {
        val name = fileName ?: "${DateUtils.currentTimeMillis()}.jpg"
        val dir = "${getCacheDirectory()}/images"
        fileManager.createDirectoryAtPath(dir, withIntermediateDirectories = true, attributes = null, error = null)
        val path = "$dir/$name"
        val url = NSURL.fileURLWithPath(path)
        NSData.create(bytes = null, length = 0u).writeToURL(url, atomically = true)
        File(url)
    }

    override suspend fun createTempFile(fileName: String?): File = withContext(MyDispatchers.IO) {
        val name = fileName ?: "${DateUtils.currentTimeMillis()}.pdf"
        val path = "${NSTemporaryDirectory()}$name"
        val url = NSURL.fileURLWithPath(path)
        NSData.create(bytes = null, length = 0u).writeToURL(url, atomically = true)
        File(url)
    }

    override suspend fun createFileFromBytes(fileName: String?, bytes: ByteArray): File =
        withContext(MyDispatchers.IO) {
            val file = createFile(fileName)

            try {
                val data = bytes.toNSData()
                data.writeToURL(file.url, atomically = true)
                file
            } catch (e: Exception) {
                L.e(e, "Failed to write bytes to file: ${file.getName()}")
                throw e
            }
        }

    override suspend fun writeFileBytes(file: File, bytes: ByteArray): Boolean =
        withContext(MyDispatchers.IO) {
            try {
                val data = bytes.toNSData()
                data.writeToURL(file.url, atomically = true)
                true
            } catch (e: Exception) {
                L.e(e, "Failed to write file: ${file.getPath()}")
                false
            }
        }

    override suspend fun readFileBytes(file: File): ByteArray? = withContext(MyDispatchers.IO) {
        try {
            if (!file.exists()) {
                L.e("File not found: ${file.getPath()}")
                return@withContext null
            }

            val data = NSData.dataWithContentsOfURL(file.url)
            data?.toByteArray()
        } catch (e: Exception) {
            L.e(e, "Failed to read file: ${file.getPath()}")
            null
        }
    }

    override suspend fun copyFile(source: File, destinationPath: String): File? =
        withContext(MyDispatchers.IO) {
            try {
                val sourceUrl = source.url
                val destUrl = NSURL.fileURLWithPath(destinationPath)

                fileManager.copyItemAtURL(sourceUrl, destUrl, error = null)

                File(destUrl)
            } catch (e: Exception) {
                L.e(e, "Failed to copy file: ${source.getPath()} -> $destinationPath")
                null
            }
        }

    override fun getCacheDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSCachesDirectory,
            NSUserDomainMask,
            true
        )
        return paths.firstOrNull() as? String ?: NSTemporaryDirectory()
    }

    override fun getDocumentsDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        )
        return paths.firstOrNull() as? String ?: ""
    }

    override suspend fun saveImage(
        image: Image,
        fileName: String,
        directoryPath: String?
    ): File = withContext(MyDispatchers.IO) {
        val dir = directoryPath ?: getCacheDirectory()
        val path = "$dir/$fileName"
        val url = NSURL.fileURLWithPath(path)

        try {
            val bytes = image.toByteArray(ImageFormat.JPEG, QUALITY)
            val data = bytes.toNSData()
            data.writeToURL(url, atomically = true)

            File(url)
        } catch (e: Exception) {
            L.e(e, "Failed to save image: $fileName")
            throw e
        }
    }

    override suspend fun createImagePreview(file: File, size: Int): ImagePreview? {
        return try {
            loadImage(file)?.createPreview(size) ?: file.getThumbnail(size)
        } catch (e: Exception) {
            L.e(e, "Failed to create preview: ${file.getPath()}")
            null
        }
    }

    override suspend fun saveImageToGallery(image: Image): File? = withContext(MyDispatchers.Main) {
        try {
            val bytes = image.toByteArray(ImageFormat.JPEG, QUALITY)
            val data = bytes.toNSData()
            val uiImage = UIImage.imageWithData(data) ?: return@withContext null
            UIImageWriteToSavedPhotosAlbum(uiImage, null, null, null)
            val name = "IMG_${DateUtils.currentTimeMillis()}.jpg"
            val path = "${getCacheDirectory()}/images/$name"
            fileManager.createDirectoryAtPath(
                "${getCacheDirectory()}/images",
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
            val url = NSURL.fileURLWithPath(path)
            data.writeToURL(url, atomically = true)
            File(url)
        } catch (e: Exception) {
            L.e(e, "Failed to save image to gallery")
            null
        }
    }

    override suspend fun saveFileToDownloads(file: File): File? = withContext(MyDispatchers.IO) {
        try {
            val downloadsDir = "${getDocumentsDirectory()}/Downloads"
            fileManager.createDirectoryAtPath(
                downloadsDir,
                withIntermediateDirectories = true,
                attributes = null,
                error = null,
            )
            val destPath = "$downloadsDir/${file.getName()}"
            val destUrl = NSURL.fileURLWithPath(destPath)
            fileManager.copyItemAtURL(file.url, destUrl, error = null)
            File(destUrl)
        } catch (e: Exception) {
            L.e(e, "Failed to save file to downloads")
            null
        }
    }

    private suspend fun File.getThumbnail(size: Int): ImagePreview? = withContext(MyDispatchers.Default) {
        try {
            val image = UIImage.imageWithContentsOfFile(getPath()) ?: return@withContext null

            val originalWidth = image.size.useContents { width }
            val originalHeight = image.size.useContents { height }

            val scale = minOf(size.toDouble() / originalWidth, size.toDouble() / originalHeight)
            val newWidth = (originalWidth * scale).toInt()
            val newHeight = (originalHeight * scale).toInt()

            UIGraphicsBeginImageContextWithOptions(
                CGSizeMake(newWidth.toDouble(), newHeight.toDouble()),
                false,
                1.0
            )

            image.drawInRect(CGRectMake(0.0, 0.0, newWidth.toDouble(), newHeight.toDouble()))
            val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()

            resizedImage?.let {
                val data = UIImageJPEGRepresentation(it, COMPRESSION_QUALITY) ?: return@withContext null
                ImagePreview(
                    data = data.toByteArray(),
                    width = newWidth,
                    height = newHeight
                )
            }
        } catch (e: Exception) {
            L.e(e, "Failed to get thumbnail: ${getPath()}")
            null
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = this.size.toULong()
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}
