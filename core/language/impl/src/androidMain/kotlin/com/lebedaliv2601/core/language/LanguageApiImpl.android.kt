package com.lebedaliv2601.core.language

import com.lebedaliv2601.core.language.api.AppLanguage
import com.lebedaliv2601.core.language.api.LanguageApi
import com.lebedaliv2601.core.language.storage.LanguageStorage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.runBlocking
import java.util.Locale

@SingleIn(AppScope::class)
@Inject
internal actual class LanguageApiImpl(
    private val languageStorage: LanguageStorage,
) : LanguageApi {

    private companion object {
        var appLanguage: AppLanguage? = null
    }

    actual override fun currentLanguage(): AppLanguage {
        return appLanguage ?: run {
            runBlocking {
                val languageFromPrefs = languageStorage.getLanguage(Locale.getDefault().language)
                filterLanguageWithAvailable(languageFromPrefs).also { appLanguage = it }
            }
        }
    }

    private fun filterLanguageWithAvailable(language: String): AppLanguage {
        val availableLanguages = getAvailableLanguages()
        return if (availableLanguages.contains(language)) AppLanguage.fromValue(language)
        else when (language) {
            "ru", "be", "kk", "uz", "ky", "hy" -> AppLanguage.Russian
            else -> {
                if (availableLanguages.contains(AppLanguage.English.value)) AppLanguage.English
                else AppLanguage.Russian
            }
        }
    }

    private fun getAvailableLanguages(): List<String> {
        val availableLanguages = AppLanguage.entries.map { it.value }
        return availableLanguages
    }
}