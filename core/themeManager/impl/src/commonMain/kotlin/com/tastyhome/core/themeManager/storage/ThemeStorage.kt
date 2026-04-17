package com.tastyhome.core.themeManager.storage

import androidx.datastore.preferences.core.edit
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ThemeStorage(
    private val themeDataStore: ThemeDataStore
) {
    suspend fun saveTheme(theme: String) {
        withContext(MyDispatchers.IO) {
            themeDataStore.edit { it[THEME_TAG_PREFERENCE] = theme }
        }
    }

    fun observeTheme(default: String): Flow<String> {
        return themeDataStore.data.map { it[THEME_TAG_PREFERENCE] ?: default }
    }
}