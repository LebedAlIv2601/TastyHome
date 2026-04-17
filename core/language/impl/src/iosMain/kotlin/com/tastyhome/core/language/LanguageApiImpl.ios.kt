package com.tastyhome.core.language

import com.tastyhome.core.language.api.AppLanguage
import com.tastyhome.core.language.api.LanguageApi
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

internal actual class LanguageApiImpl : LanguageApi {
    actual override fun currentLanguage(): AppLanguage {
        return AppLanguage.fromValue(NSLocale.currentLocale().languageCode)
    }
}