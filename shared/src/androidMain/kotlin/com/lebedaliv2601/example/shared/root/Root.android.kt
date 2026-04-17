package com.lebedaliv2601.example.shared.root

import android.content.res.Configuration
import android.content.res.Resources
import com.lebedaliv2601.core.themeManager.api.AppTheme

internal actual fun isDarkTheme(theme: AppTheme): Boolean {
    return when (theme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.System -> {
            (Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        }
    }
}