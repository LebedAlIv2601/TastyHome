package com.tastyhome.base.platform.camera

import com.tastyhome.base.platform.file.image.Image

interface CameraManager {
    suspend fun takePhoto(): Image?
}
