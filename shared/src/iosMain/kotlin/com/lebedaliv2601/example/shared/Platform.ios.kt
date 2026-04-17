package com.lebedaliv2601.example.shared

import com.lebedaliv2601.base.platform.IosPlatform
import com.lebedaliv2601.base.platform.appInfo.IosAppFlavors
import com.lebedaliv2601.base.platform.Platform
import platform.UIKit.UIViewController

fun getPlatform(viewController: UIViewController, flavors: IosAppFlavors): Platform {
    return IosPlatform(viewController, flavors)
}
