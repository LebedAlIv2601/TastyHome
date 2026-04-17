package com.tastyhome.base.platform.file.image

/**
 * Легковесный класс для хранения превью изображения.
 * 
 * Используется в FileData для отображения миниатюр без загрузки полного изображения.
 * Содержит только минимум данных: сжатое изображение в ByteArray и размеры.
 * 
 * Преимущества:
 * - Малый размер (~10-50 KB против нескольких MB для полного изображения)
 * - Быстрая передача между компонентами
 * - Нет избыточных методов обработки изображений
 * 
 * @param data Сжатые данные изображения (обычно JPEG с низким качеством)
 * @param width Ширина превью в пикселях
 * @param height Высота превью в пикселях
 */
data class ImagePreview(
    val data: ByteArray,
    val width: Int,
    val height: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        
        other as ImagePreview
        
        if (!data.contentEquals(other.data)) return false
        if (width != other.width) return false
        if (height != other.height) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
    
    override fun toString(): String = "ImagePreview(size=${data.size} bytes, ${width}x$height)"
}
