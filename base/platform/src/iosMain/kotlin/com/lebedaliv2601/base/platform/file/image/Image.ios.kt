package com.lebedaliv2601.base.platform.file.image

import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import com.lebedaliv2601.base.foundation.date.DateUtils
import com.lebedaliv2601.base.platform.file.File
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

private const val COMPRESSION_QUALITY = 0.8
private const val PERCENTAGE = 100.0

@OptIn(ExperimentalForeignApi::class)
actual class Image(
    url: NSURL,
    val uiImage: UIImage
) : File(url) {
    
    actual fun toByteArray(format: ImageFormat, quality: Int): ByteArray {
        val data = when (format) {
            ImageFormat.JPEG -> UIImageJPEGRepresentation(uiImage, quality / PERCENTAGE)
            ImageFormat.PNG -> UIImagePNGRepresentation(uiImage)
        }
        
        return data?.toByteArray() ?: ByteArray(0)
    }
    
    actual fun getWidth(): Int = uiImage.size.useContents { width.toInt() }
    
    actual fun getHeight(): Int = uiImage.size.useContents { height.toInt() }
    
    actual suspend fun resize(maxWidth: Int, maxHeight: Int): Image = withContext(MyDispatchers.Default) {
        val currentWidth = uiImage.size.useContents { width }
        val currentHeight = uiImage.size.useContents { height }
        
        if (currentWidth <= maxWidth && currentHeight <= maxHeight) {
            return@withContext this@Image
        }
        
        val scale = minOf(
            maxWidth.toDouble() / currentWidth,
            maxHeight.toDouble() / currentHeight
        )
        
        val newWidth = currentWidth * scale
        val newHeight = currentHeight * scale
        val newSize = CGSizeMake(newWidth, newHeight)
        
        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        uiImage.drawInRect(CGRectMake(0.0, 0.0, newWidth, newHeight))
        val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        Image(url, resizedImage ?: uiImage)
    }
    
    actual suspend fun createPreview(size: Int): ImagePreview = withContext(MyDispatchers.Default) {
        val thumbnailSize = CGSizeMake(size.toDouble(), size.toDouble())
        
        UIGraphicsBeginImageContextWithOptions(thumbnailSize, false, 0.0)
        uiImage.drawInRect(CGRectMake(0.0, 0.0, size.toDouble(), size.toDouble()))
        val thumbnail = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        val data = UIImageJPEGRepresentation(thumbnail ?: uiImage, COMPRESSION_QUALITY)?.toByteArray() ?: ByteArray(0)

        ImagePreview(
            data = data,
            width = size,
            height = size
        )
    }
    
    override fun toString(): String = "Image(path=${getPath()}, ${getWidth()}x${getHeight()})"
}

@OptIn(ExperimentalForeignApi::class)
actual object PlatformImageFactory {
    
    actual suspend fun fromByteArray(bytes: ByteArray): Image? = withContext(MyDispatchers.Default) {
        val data = bytes.toNSData()
        val image = UIImage(data = data)
        
        val tempUrl = createTempFileUrl()
        Image(tempUrl, image)
    }
    
    actual suspend fun fromFile(file: File): Image? = withContext(MyDispatchers.Default) {
        val image = UIImage.imageWithContentsOfFile(file.getPath()) ?: return@withContext null
        Image(file.url, image)
    }
    
    private fun createTempFileUrl(): NSURL {
        val tempDir = NSTemporaryDirectory()
        val fileName = "image_${DateUtils.currentTimeMillis()}.jpg"
        return NSURL.fileURLWithPath("$tempDir$fileName")
    }
}

@OptIn(ExperimentalForeignApi::class)
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
