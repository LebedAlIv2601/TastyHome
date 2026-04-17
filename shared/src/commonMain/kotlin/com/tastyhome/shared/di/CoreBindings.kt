package com.tastyhome.shared.di

import com.tastyhome.core.database.di.DatabaseBindings
import com.tastyhome.core.language.di.LanguageBindings
import com.tastyhome.core.network.baseClient.di.NetworkBindings
import com.tastyhome.core.themeManager.di.ThemeManagerBindings
import dev.zacsweers.metro.BindingContainer

@BindingContainer(
    includes = [
        LanguageBindings::class,
        DatabaseBindings::class,
        NetworkBindings::class,
        ThemeManagerBindings::class,
    ]
)
internal interface CoreBindings