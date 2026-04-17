package com.tastyhome.shared.di

import android.content.Context
import com.tastyhome.base.network.httpClient.models.domain.NetworkEnvironment
import com.tastyhome.base.platform.Platform
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory

@DependencyGraph(AppScope::class, bindingContainers = [SharedBindings::class, StorageBindings::class])
interface AndroidAppGraph : AppGraph {

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides platform: Platform,
            @Provides context: Context,
            @Provides environment: NetworkEnvironment,
        ): AndroidAppGraph
    }
}

fun createRootGraph(
    context: Context,
    platform: Platform,
    environment: NetworkEnvironment,
): AndroidAppGraph {
    return createGraphFactory<AndroidAppGraph.Factory>().create(platform, context, environment)
}