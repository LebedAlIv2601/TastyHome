package com.lebedaliv2601.core.language.di

import com.lebedaliv2601.core.language.LanguageApiImpl
import com.lebedaliv2601.core.language.api.LanguageApi
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
abstract class IosLanguageBindings {

    @Binds
    internal abstract val LanguageApiImpl.bind: LanguageApi
}

actual typealias LanguageBindings = IosLanguageBindings
