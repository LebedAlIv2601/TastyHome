package com.tastyhome.core.language.storage

import androidx.datastore.preferences.core.edit
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Inject
internal class LanguageStorage(
    private val languageDataStore: LanguageDataStore
) {
    suspend fun saveLanguage(language: String) {
        withContext(MyDispatchers.IO) {
            languageDataStore.edit { it[LANGUAGE_TAG_PREFERENCE] = language }
        }
    }

    suspend fun getLanguage(default: String): String {
        return languageDataStore.data.map { it[LANGUAGE_TAG_PREFERENCE] ?: default }.first()
    }
}