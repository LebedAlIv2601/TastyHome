package com.tastyhome.base.platform.file.image

/**
 * Формат изображения для экспорта/сохранения
 */
enum class ImageFormat {
    /**
     * JPEG формат (сжатие с потерями, меньший размер)
     */
    JPEG,
    
    /**
     * PNG формат (без потерь, поддержка прозрачности)
     */
    PNG
}
