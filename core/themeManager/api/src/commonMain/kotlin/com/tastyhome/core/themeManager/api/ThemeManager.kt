package com.tastyhome.core.themeManager.api

import kotlinx.coroutines.flow.Flow

interface ThemeManager {

    fun observeTheme(): Flow<AppTheme>

    suspend fun setTheme(theme: AppTheme)
}