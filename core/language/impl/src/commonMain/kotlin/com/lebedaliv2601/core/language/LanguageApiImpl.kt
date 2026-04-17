package com.lebedaliv2601.core.language

import com.lebedaliv2601.core.language.api.AppLanguage
import com.lebedaliv2601.core.language.api.LanguageApi

internal expect class LanguageApiImpl : LanguageApi {
    override fun currentLanguage(): AppLanguage
}