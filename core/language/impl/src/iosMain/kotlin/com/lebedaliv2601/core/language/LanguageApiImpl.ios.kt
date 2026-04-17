package com.lebedaliv2601.core.language

import com.lebedaliv2601.core.language.api.AppLanguage
import com.lebedaliv2601.core.language.api.LanguageApi
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

internal actual class LanguageApiImpl : LanguageApi {
    actual override fun currentLanguage(): AppLanguage {
        return AppLanguage.fromValue(NSLocale.currentLocale().languageCode)
    }
}