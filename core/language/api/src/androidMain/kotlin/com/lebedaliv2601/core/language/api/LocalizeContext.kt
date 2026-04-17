package com.lebedaliv2601.core.language.api

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Метод для подмены локали в контексте. Должна вызываться в
 * attachBaseContext в Application и Activity для получения
 * правильной локали в приложении
 * @param context контекст, в котором необходимо изменить язык
 * @return измененнный контекст [Context]
 */
fun LanguageApi.localizeContext(context: Context?): Context? {
    val configuration = Configuration(context?.resources?.configuration)
    val newLocale = Locale.forLanguageTag(currentLanguage().value)
    Locale.setDefault(newLocale)
    configuration.setLocale(newLocale)
    configuration.setLayoutDirection(newLocale)
    return context?.createConfigurationContext(configuration)
}