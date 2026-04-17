package com.tastyhome.shared.language

import android.content.Context
import com.tastyhome.core.language.LanguageDependenciesProvider
import com.tastyhome.core.language.api.localizeContext

fun updateContextWithActualLanguage(context: Context): Context? {
    return LanguageDependenciesProvider.provideLanguageApi(context)
        .localizeContext(context)
}