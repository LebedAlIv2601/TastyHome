package com.tastyhome.shared.root

import com.tastyhome.core.themeManager.api.AppTheme
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

internal actual fun isDarkTheme(theme: AppTheme): Boolean {
    return when (theme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.System -> {
            UIScreen.mainScreen.traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
        }
    }
}
