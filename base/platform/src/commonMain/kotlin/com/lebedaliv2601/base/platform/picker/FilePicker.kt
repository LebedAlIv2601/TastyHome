package com.lebedaliv2601.base.platform.picker

import com.lebedaliv2601.base.platform.file.File

/**
 * FilePicker для выбора файлов через нативные пикеры.
 *
 * Использует современные платформенные подходы:
 * - iOS: PHPickerViewController для медиа, UIDocumentPickerViewController для файлов
 * - Android: Activity Result API с правильными MIME-типами
 *
 * Требует Activity (Android) или UIViewController (iOS) для презентации UI.
 */
interface FilePicker {

    /**
     * Выбор медиа-файлов (фото/видео).
     *
     * iOS: использует PHPickerViewController (iOS 14+)
     * Android: использует PickVisualMedia/PickMultipleVisualMedia
     *
     * @param allowMultiple Разрешить множественный выбор
     * @param mediaType Тип медиа (IMAGE, VIDEO или ALL)
     * @return Список выбранных файлов (пустой если отмена)
     */
    suspend fun pickMedia(
        allowMultiple: Boolean = false,
        mediaType: MediaType = MediaType.IMAGE
    ): List<File>

    /**
     * Выбор любых файлов.
     *
     * iOS: использует UIDocumentPickerViewController
     * Android: использует GetContent/GetMultipleContents с mime types
     *
     * @param allowMultiple Разрешить множественный выбор
     * @param mimeTypes Список MIME-типов для фильтрации (например: ["application/pdf", "image/ *"])
     * @return Список выбранных файлов (пустой если отмена)
     */
    suspend fun pickFile(
        allowMultiple: Boolean = false,
        mimeTypes: List<String> = listOf("*/*")
    ): List<File>
}
