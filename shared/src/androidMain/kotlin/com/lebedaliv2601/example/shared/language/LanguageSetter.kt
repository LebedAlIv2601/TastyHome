package com.lebedaliv2601.example.shared.language

import android.content.Context
import com.lebedaliv2601.core.language.LanguageDependenciesProvider
import com.lebedaliv2601.core.language.api.localizeContext

fun updateContextWithActualLanguage(context: Context): Context? {
    return LanguageDependenciesProvider.provideLanguageApi(context)
        .localizeContext(context)
}