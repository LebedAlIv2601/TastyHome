package com.tastyhome.shared.di

import com.tastyhome.base.logger.LogSender
import com.tastyhome.shared.AppDelegate
import com.tastyhome.shared.AppDelegateImpl
import com.tastyhome.shared.root.RootComponent
import com.tastyhome.shared.logger.KermitLogSender
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