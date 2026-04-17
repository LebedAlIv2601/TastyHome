package com.lebedaliv2601.base.platform.file

import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import com.lebedaliv2601.base.logger.L
import com.lebedaliv2601.base.platform.file.image.Image
import com.lebedaliv2601.base.platform.file.image.ImagePreview
import com.lebedaliv2601.base.platform.file.image.PlatformImageFactory
import kotlinx.coroutines.withContext

/**
 * Единый менеджер для работы с файловой системой.
 *
 * Включает операции как с обычными файлами, так и с изображениями.
 * Все операции с изображениями выполняются через тот же менеджер,
 * так как изображение это файл с дополнительной функциональностью.
 */
interface FileManager {

    // ========================================
    // Базовые операции с файлами
    // ========================================

    /**
     * Создать новый файл в кэше (Android: cache/files/, iOS: cache).
     *
     * @param fileName Имя файла (если null, будет сгенерировано автоматически)
     * @return Созданный файл
     */
    suspend fun createFile(fileName: String? = null): File

    /**
     * Создать файл для изображения в кэше (Android: cache/images/, iOS: cache).
     */
    suspend fun createImageFile(fileName: String?): File

    /**
     * Создать файл во временном хранилище (Android: cache/tmp через createTempFile, iOS: NSTemporaryDirectory).
     */
    suspend fun createTempFile(fileName: String?): File

    /**
     * Создать файл из массива байтов (в кэше, Android: cache/files/).
     *
     * @param fileName Имя файла
     * @param bytes Данные для записи
     * @return Созданный файл с данными
     */
    suspend fun createFileFromBytes(fileName: String?, bytes: ByteArray): File

    /**
     * Записать байты в существующий файл.
     *
     * @return true при успехе
     */
    suspend fun writeFileBytes(file: File, bytes: ByteArray): Boolean

    /**
     * Прочитать файл как массив байтов
     *
     * @param file Файл для чтения
     * @return Данные файла или null если файл не существует/ошибка чтения
     */
    suspend fun readFileBytes(file: File): ByteArray?

    /**
     * Удалить файл
     *
     * @param file Файл для удаления
     * @return true если файл был удален успешно
     */
    suspend fun deleteFile(file: File): Boolean = withContext(MyDispatchers.IO) {
        try {
            file.delete()
        } catch (e: Exception) {
            L.e(e, "Failed to delete file: ${file.getPath()}")
            false
        }
    }

    /**
     * Скопировать файл в новое место
     *
     * @param source Исходный файл
     * @param destinationPath Путь к новому файлу
     * @return Новый файл или null при ошибке
     */
    suspend fun copyFile(source: File, destinationPath: String): File?

    // ========================================
    // Директории
    // ========================================

    /**
     * Получить путь к директории кэша приложения
     */
    fun getCacheDirectory(): String

    /**
     * Получить путь к директории документов приложения
     */
    fun getDocumentsDirectory(): String

    // ========================================
    // Работа с изображениями
    // ========================================

    /**
     * Загрузить изображение из файла
     *
     * @param file Файл изображения
     * @return PlatformImage или null если файл не является изображением
     */
    suspend fun loadImage(file: File): Image? {
        return try {
            PlatformImageFactory.fromFile(file)
        } catch (e: Exception) {
            L.e(e, "Failed to load image: ${file.getPath()}")
            null
        }
    }

    /**
     * Сохранить изображение в файл
     *
     * @param image Изображение для сохранения
     * @param fileName Имя файла
     * @param directoryPath Путь к директории (если null, используется кэш)
     * @return Файл с сохраненным изображением
     */
    suspend fun saveImage(
        image: Image,
        fileName: String,
        directoryPath: String? = null
    ): File

    /**
     * Создать превью для изображения
     *
     * @param file Файл изображения
     * @param size Размер превью (квадратное size x size)
     * @return ImagePreview или null если не удалось создать
     */
    suspend fun createImagePreview(file: File, size: Int = 64): ImagePreview?

    /**
     * Сохранить изображение в галерею (фото).
     *
     * @return Файл сохранённого изображения или null при ошибке/отказе
     */
    suspend fun saveImageToGallery(image: Image): File?

    /**
     * Сохранить файл в папку «Загрузки» (Downloads).
     *
     * @return Файл в загрузках или null при ошибке/отказе
     */
    suspend fun saveFileToDownloads(file: File): File?
}