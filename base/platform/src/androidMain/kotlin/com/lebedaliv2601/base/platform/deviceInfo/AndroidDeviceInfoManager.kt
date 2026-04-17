package com.lebedaliv2601.base.platform.deviceInfo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.lebedaliv2601.base.foundation.other.randomUuid

internal class AndroidDeviceInfoManager(
    private val context: Context
) : DeviceInfoManager {

    override val deviceId: String = getAppDeviceIdWithRetry(3)

    override val model: String = Build.MODEL

    override val os: String = "android"

    override val osVersion: String = Build.VERSION.SDK_INT.toString()

    @SuppressLint("HardwareIds")
    private fun getAppDeviceIdWithRetry(maxRetries: Int): String {
        repeat(maxRetries) { attempt ->
            try {
                val androidId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                ).ifEmpty { null }

                return androidId.orEmpty().ifEmpty { randomUuid() }
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) {
                    return randomUuid()
                }
            }
        }

        return randomUuid()
    }
}