package com.lebedaliv2601.base.platform.file

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import com.lebedaliv2601.base.logger.L
import com.lebedaliv2601.base.platform.file.image.ImageFormat
import com.lebedaliv2601.base.platform.file.image.ImagePreview
import kotlinx.coroutines.withContext
import com.lebedaliv2601.base.platform.file.image.Image
import java.io.ByteArrayOutputStream
import java.io.File as JavaFile
import java.io.FileOutputStream

private const val IMAGE_JPEG = "image/jpeg"
private const val IMAGE_QUALITY = 90
private const val IMAGES = "images"
private const val FILES = "files"

internal class AndroidFileManager(private val context: Context) : FileManager {

    override suspend fun createFile(fileName: String?): File = withContext(MyDispatchers.IO) {
        val name = fileName ?: "${System.currentTimeMillis()}.pdf"
        val dir = JavaFile(context.cacheDir, FILES).apply { mkdirs() }
        val file = JavaFile(dir, name).apply { createNewFile() }
        File(file)
    }

    override suspend fun createImageFile(fileName: String?): File = withContext(MyDispatchers.IO) {
        val name = fileName ?: "${System.currentTimeMillis()}.jpg"
        val dir = JavaFile(context.cacheDir, IMAGES).apply { mkdirs() }
        val file = JavaFile(dir, name).apply { createNewFile() }
        File(file)
    }

    override suspend fun createTempFile(fileName: String?): File = withContext(MyDispatchers.IO) {
        val name = fileName ?: "${System.currentTimeMillis()}.tmp"
        val suffix = if (name.contains('.')) ".${name.substringAfterLast('.')}" else ".tmp"
        val prefix = name.substringBeforeLast('.').takeIf { it.isNotEmpty() }?.plus("_") ?: "tmp_"
        val tmpDir = JavaFile(context.cacheDir, "tmp").apply { mkdirs() }
        val file = JavaFile.createTempFile(prefix, suffix, tmpDir)
        file.deleteOnExit()
        File(file)
    }

    override suspend fun createFileFromBytes(fileName: String?, bytes: ByteArray): File {
        return withContext(MyDispatchers.IO) {
            val f = createFile(fileName)
            f.file.writeBytes(bytes)
            f
        }
    }

    override suspend fun writeFileBytes(file: File, bytes: ByteArray): Boolean =
        withContext(MyDispatchers.IO) {
            try {
                file.file.writeBytes(bytes)
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

            file.file.inputStream().use { input ->
                input.readBytes()
            }
        } catch (e: Exception) {
            L.e(e, "Failed to read file: ${file.getPath()}")
            null
        }
    }

    override suspend fun copyFile(source: File, destinationPath: String): File? =
        withContext(MyDispatchers.IO) {
            try {
                val sourceFile = source.file
                val destFile = JavaFile(destinationPath)

                destFile.parentFile?.mkdirs()
                sourceFile.copyTo(destFile, overwrite = true)

                File(destFile)
            } catch (e: Exception) {
                L.e(e, "Failed to copy file: ${source.getPath()} -> $destinationPath")
                null
            }
        }

    override fun getCacheDirectory(): String = context.cacheDir.absolutePath

    override fun getDocumentsDirectory(): String = context.filesDir.absolutePath

    override suspend fun saveImage(
        image: Image,
        fileName: String,
        directoryPath: String?
    ): File = withContext(MyDispatchers.IO) {
        val baseDir = directoryPath ?: getCacheDirectory()
        val dirFile = JavaFile(baseDir, IMAGES).apply { mkdirs() }
        val file = JavaFile(dirFile, fileName)

        try {
            FileOutputStream(file).use { output ->
                val bytes = image.toByteArray(ImageFormat.JPEG, IMAGE_QUALITY)
                output.write(bytes)
                output.flush()
            }
            File(file)
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

    override suspend fun saveImageToGallery(image: Image): File? = withContext(MyDispatchers.IO) {
        try {
            val bytes = image.toByteArray(ImageFormat.JPEG, IMAGE_QUALITY)
            val name = "IMG_${System.currentTimeMillis()}.jpg"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, IMAGE_JPEG)
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return@withContext null
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(bytes)
            }
            val dirFile = JavaFile(context.cacheDir, IMAGES).apply { mkdirs() }
            val copyFile = JavaFile(dirFile, name).apply {
                writeBytes(bytes)
            }
            notifySystemAbout(copyFile)
            File(copyFile)
        } catch (e: Exception) {
            L.e(e, "Failed to save image to gallery")
            null
        }
    }

    override suspend fun saveFileToDownloads(file: File): File? = withContext(MyDispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName())
                    put(MediaStore.MediaColumns.MIME_TYPE, resolveMimeType(file.getExtension()))
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return@withContext null
                val bytes = readFileBytes(file) ?: return@withContext null
                context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                val dirFile = JavaFile(context.cacheDir, FILES).apply { mkdirs() }
                val copyFile = JavaFile(dirFile, file.getName()).apply { writeBytes(bytes) }
                notifySystemAbout(copyFile)
                File(copyFile)
            } else {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!dir.exists()) dir.mkdirs()
                val dest = JavaFile(dir, file.getName())
                file.file.copyTo(dest, overwrite = true)
                notifySystemAbout(dest)
                File(dest)
            }
        } catch (e: Exception) {
            L.e(e, "Failed to save file to downloads")
            null
        }
    }

    private suspend fun File.getThumbnail(size: Int): ImagePreview? = withContext(MyDispatchers.IO) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val uri = Uri.fromFile(file)
                context.contentResolver.loadThumbnail(uri, Size(size, size), null)
            } else {
                null
            }

            bitmap?.let {
                val stream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream)
                ImagePreview(
                    data = stream.toByteArray(),
                    width = it.width,
                    height = it.height
                )
            }
        } catch (e: Exception) {
            L.e(e, "Failed to get thumbnail: ${file.path}")
            null
        }
    }

    fun notifySystemAbout(file: JavaFile) {
        val mimeType = resolveMimeType(file.extension)

        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf(mimeType),
            null,
        )
    }

    fun clearTempFiles() {
        JavaFile(context.cacheDir, "tmp").deleteRecursively()
    }

    private fun resolveMimeType(ext: String): String = when (ext.lowercase()) {
        "jpg", "jpeg" -> IMAGE_JPEG
        "png" -> "image/png"
        "pdf" -> "application/pdf"
        "mp4" -> "video/mp4"
        "txt" -> "text/plain"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "xls" -> "application/vnd.ms-excel"
        "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        "ppt" -> "application/vnd.ms-powerpoint"
        "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        "zip" -> "application/zip"
        "rar" -> "application/x-rar-compressed"
        else -> "*/*"
    }
}
