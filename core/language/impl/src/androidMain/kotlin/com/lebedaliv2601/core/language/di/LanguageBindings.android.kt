package com.lebedaliv2601.core.language.di

import android.content.Context
import com.lebedaliv2601.core.language.ChangeLanguageApiImpl
import com.lebedaliv2601.core.language.LanguageApiImpl
import com.lebedaliv2601.core.language.LanguageDependenciesProvider
import com.lebedaliv2601.core.language.api.ChangeLanguageApi
import com.lebedaliv2601.core.language.api.LanguageApi
import com.lebedaliv2601.core.language.storage.LanguageDataStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
abstract class AndroidLanguageBindings {

    @Binds
    internal abstract val LanguageApiImpl.bind: LanguageApi

    @Binds
    internal abstract val ChangeLanguageApiImpl.bind: ChangeLanguageApi

    companion object {
        @SingleIn(AppScope::class)
        @Provides
        private fun provideDataStore(context: Context): LanguageDataStore =
            LanguageDependenciesProvider.provideDataStore(context)
    }
}

actual typealias LanguageBindings = AndroidLanguageBindings