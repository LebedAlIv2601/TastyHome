package com.tastyhome.core.themeManager.di

import com.tastyhome.base.localStorage.DataStoreFactory
import com.tastyhome.core.themeManager.ThemeManagerImpl
import com.tastyhome.core.themeManager.api.ThemeManager
import com.tastyhome.core.themeManager.storage.ThemeDataStore
import com.tastyhome.core.themeManager.storage.ThemeStorage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
abstract class ThemeManagerBindings {

    @Binds
    internal abstract val ThemeManagerImpl.bind: ThemeManager

    companion object {
        @SingleIn(AppScope::class)
        @Provides
        internal fun provideStorage(
            factory: DataStoreFactory,
        ): ThemeStorage {
            return ThemeStorage(ThemeDataStore(factory.create(filename = "theme_store")))
        }
    }
}