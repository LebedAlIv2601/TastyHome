package com.lebedaliv2601.core.themeManager

import com.lebedaliv2601.core.themeManager.api.AppTheme
import com.lebedaliv2601.core.themeManager.api.ThemeManager
import com.lebedaliv2601.core.themeManager.storage.ThemeStorage
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
internal class ThemeManagerImpl(
    private val storage: ThemeStorage
) : ThemeManager {
    override fun observeTheme(): Flow<AppTheme> {
        return storage.observeTheme(AppTheme.System.value).map { AppTheme.fromValue(it) }
    }

    override suspend fun setTheme(theme: AppTheme) {
        storage.saveTheme(theme.value)
    }
}