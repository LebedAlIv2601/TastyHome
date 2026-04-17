package com.tastyhome.core.language

import com.tastyhome.core.language.api.AppLanguage
import com.tastyhome.core.language.api.LanguageApi

internal expect class LanguageApiImpl : LanguageApi {
    override fun currentLanguage(): AppLanguage
}