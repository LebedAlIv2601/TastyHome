package com.lebedaliv2601.base.platform.camera

import com.lebedaliv2601.base.platform.file.image.Image

interface CameraManager {
    suspend fun takePhoto(): Image?
}
