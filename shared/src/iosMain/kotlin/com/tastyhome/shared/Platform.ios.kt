package com.tastyhome.shared

import com.tastyhome.base.platform.IosPlatform
import com.tastyhome.base.platform.appInfo.IosAppFlavors
import com.tastyhome.base.platform.Platform
import platform.UIKit.UIViewController

fun getPlatform(viewController: UIViewController, flavors: IosAppFlavors): Platform {
    return IosPlatform(viewController, flavors)
}
