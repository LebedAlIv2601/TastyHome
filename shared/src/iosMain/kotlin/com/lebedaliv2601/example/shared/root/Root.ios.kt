package com.lebedaliv2601.example.shared.root

import com.lebedaliv2601.core.themeManager.api.AppTheme
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
