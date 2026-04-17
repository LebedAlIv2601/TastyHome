package com.lebedaliv2601.example.shared.di

import com.lebedaliv2601.core.database.di.DatabaseBindings
import com.lebedaliv2601.core.language.di.LanguageBindings
import com.lebedaliv2601.core.network.baseClient.di.NetworkBindings
import com.lebedaliv2601.core.themeManager.di.ThemeManagerBindings
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