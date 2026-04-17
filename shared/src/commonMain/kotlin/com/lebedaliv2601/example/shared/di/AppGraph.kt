package com.lebedaliv2601.example.shared.di

import com.lebedaliv2601.base.logger.LogSender
import com.lebedaliv2601.example.shared.AppDelegate
import com.lebedaliv2601.example.shared.AppDelegateImpl
import com.lebedaliv2601.example.shared.root.RootComponent
import com.lebedaliv2601.example.shared.logger.KermitLogSender
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.IntoSet

interface AppGraph {
    val appDelegate: AppDelegate
    fun rootComponentFactory(): RootComponent.Factory
}

@BindingContainer(
    includes = [
        BaseBindings::class,
        CoreBindings::class,
        FeatureBindings::class,
    ]
)
internal interface SharedBindings {
    @Binds
    val AppDelegateImpl.bind: AppDelegate

    @Binds
    @IntoSet
    val KermitLogSender.bind: LogSender
}