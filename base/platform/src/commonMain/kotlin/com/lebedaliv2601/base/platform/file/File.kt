package com.lebedaliv2601.base.platform.file

/**
 * Базовый класс для работы с файлами в кросс-платформенном коде.
 *
 * Android: обертка над java.io.File
 * iOS: обертка над NSURL + NSFileManager
 */
expect open class File {
    /**
     * Имя файла с расширением
     */
    fun getName(): String

    /**
     * Полный путь к файлу
     */
    fun getPath(): String

    /**
     * Расширение файла без точки (например: "jpg", "pdf")
     */
    fun getExtension(): String

    /**
     * Проверяет существование файла
     */
    fun exists(): Boolean

    /**
     * Размер файла в байтах
     */
    fun size(): Long

    /**
     * Удаляет файл
     * @return true если файл был удален, false в противном случае
     */
    fun delete(): Boolean
}

/**
 * Размер файла в килобайтах
 */
val File.sizeInKb: Double
    get() = size() / 1024.0

/**
 * Размер файла в мегабайтах
 */
val File.sizeInMb: Double
    get() = sizeInKb / 1024.0

/**
 * Проверяет, является ли файл изображением
 */
val File.isImage: Boolean
    get() = getExtension().lowercase() in listOf("jpg", "jpeg", "png", "webp", "gif", "bmp")

/**
 * Проверяет, является ли файл медиа-файлом (изображение или видео)
 */
val File.isMedia: Boolean
    get() = isImage || getExtension().lowercase() in listOf("mp4", "mov", "avi", "mkv", "webm")

/**
 * Проверяет, является ли файл PDF документом
 */
val File.isPdf: Boolean
    get() = getExtension().lowercase() == "pdf"

/**
 * Проверяет, является ли файл видео
 */
val File.isVideo: Boolean
    get() = getExtension().lowercase() in listOf("mp4", "mov", "avi", "mkv", "webm", "3gp")
