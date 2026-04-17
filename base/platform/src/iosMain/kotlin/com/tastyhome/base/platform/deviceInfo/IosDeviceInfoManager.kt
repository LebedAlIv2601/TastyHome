package com.tastyhome.base.platform.deviceInfo

import com.tastyhome.base.foundation.other.randomUuid
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIDevice

@OptIn(ExperimentalForeignApi::class)
internal class IosDeviceInfoManager : DeviceInfoManager {

    override val deviceId: String = UIDevice.currentDevice.identifierForVendor
        ?.UUIDString
        .orEmpty()
        .ifEmpty { randomUuid() }

    override val model: String = UIDevice.currentDevice.model

    override val os: String = "ios"

    override val osVersion: String = UIDevice.currentDevice.systemVersion
}
