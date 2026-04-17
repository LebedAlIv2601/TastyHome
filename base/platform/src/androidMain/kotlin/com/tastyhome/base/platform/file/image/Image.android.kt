package com.tastyhome.base.platform.file.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.platform.file.File
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File as JavaFile

private const val THUMBNAIL_QUALITY = 80
private const val MAX_QUALITY = 100

actual class Image(
    file: JavaFile,
    val bitmap: Bitmap
) : File(file) {

    actual fun toByteArray(format: ImageFormat, quality: Int): ByteArray {
        val compressFormat = when (format) {
            ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
            ImageFormat.PNG -> Bitmap.CompressFormat.PNG
        }

        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(compressFormat, quality.coerceIn(0, MAX_QUALITY), stream)
            stream.toByteArray()
        }
    }

    actual fun getWidth(): Int = bitmap.width

    actual fun getHeight(): Int = bitmap.height

    actual suspend fun resize(maxWidth: Int, maxHeight: Int): Image = withContext(MyDispatchers.IO) {
        val currentWidth = bitmap.width
        val currentHeight = bitmap.height

        if (currentWidth <= maxWidth && currentHeight <= maxHeight) {
            return@withContext this@Image
        }

        val scale = minOf(
            maxWidth.toFloat() / currentWidth,
            maxHeight.toFloat() / currentHeight
        )

        val newWidth = (currentWidth * scale).toInt()
        val newHeight = (currentHeight * scale).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        Image(file, scaledBitmap)
    }

    actual suspend fun createPreview(size: Int): ImagePreview = withContext(MyDispatchers.IO) {
        val thumbnail = ThumbnailUtils.extractThumbnail(
            bitmap,
            size,
            size,
            ThumbnailUtils.OPTIONS_RECYCLE_INPUT
        )

        val data = ByteArrayOutputStream().use { stream ->
            thumbnail.compress(Bitmap.CompressFormat.JPEG, THUMBNAIL_QUALITY, stream)
            stream.toByteArray()
        }

        ImagePreview(
            data = data,
            width = thumbnail.width,
            height = thumbnail.height
        )
    }

    override fun toString(): String = "PlatformImage(path=${getPath()}, ${getWidth()}x${getHeight()})"
}

actual object PlatformImageFactory {

    actual suspend fun fromByteArray(bytes: ByteArray): Image? = withContext(MyDispatchers.IO) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext null

        val tempFile = JavaFile.createTempFile("image_", ".jpg")
        Image(tempFile, bitmap)
    }

    actual suspend fun fromFile(file: File): Image? = withContext(MyDispatchers.IO) {
        val bitmap = BitmapFactory.decodeFile(file.getPath()) ?: return@withContext null
        Image(file.file, bitmap)
    }
}
