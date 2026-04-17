package com.tastyhome.core.language.di

import com.tastyhome.core.language.LanguageApiImpl
import com.tastyhome.core.language.api.LanguageApi
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
abstract class IosLanguageBindings {

    @Binds
    internal abstract val LanguageApiImpl.bind: LanguageApi
}

actual typealias LanguageBindings = IosLanguageBindings
