package com.tastyhome.base.platform.file.image

import com.tastyhome.base.platform.file.File

/**
 * Класс для работы с изображениями в кросс-платформенном коде.
 * 
 * Наследуется от PlatformFile, так как изображение это файл с дополнительной функциональностью:
 * - Конвертация в ByteArray с выбором формата и качества
 * - Получение размеров (ширина, высота)
 * - Изменение размера (resize)
 * - Создание легковесного превью
 * 
 * Android: работает с android.graphics.Bitmap
 * iOS: работает с UIImage
 */
expect class Image : File {
    
    /**
     * Конвертирует изображение в ByteArray.
     * 
     * @param format Формат изображения (JPEG или PNG)
     * @param quality Качество сжатия (0-100, применимо для JPEG)
     * @return ByteArray с данными изображения
     */
    fun toByteArray(format: ImageFormat = ImageFormat.JPEG, quality: Int = 90): ByteArray
    
    /**
     * Получить ширину изображения в пикселях
     */
    fun getWidth(): Int
    
    /**
     * Получить высоту изображения в пикселях
     */
    fun getHeight(): Int
    
    /**
     * Изменить размер изображения с сохранением пропорций.
     * 
     * Если изображение меньше указанных размеров, оно не будет увеличено.
     * Итоговый размер будет вписан в указанные границы с сохранением пропорций.
     * 
     * @param maxWidth Максимальная ширина
     * @param maxHeight Максимальная высота
     * @return Новое изображение с измененным размером
     */
    suspend fun resize(maxWidth: Int, maxHeight: Int): Image
    
    /**
     * Создать легковесное превью изображения для отображения миниатюр.
     * 
     * @param size Размер превью (будет квадратным size x size)
     * @return ImagePreview с сжатыми данными
     */
    suspend fun createPreview(size: Int = 64): ImagePreview
}

/**
 * Фабрика для создания PlatformImage из различных источников
 */
expect object PlatformImageFactory {
    
    /**
     * Создать изображение из массива байтов
     * 
     * @param bytes Данные изображения
     * @return PlatformImage или null если не удалось декодировать
     */
    suspend fun fromByteArray(bytes: ByteArray): Image?
    
    /**
     * Создать изображение из файла
     * 
     * @param file Файл изображения
     * @return PlatformImage или null если файл не является изображением
     */
    suspend fun fromFile(file: File): Image?
}
