package com.lebedaliv2601.example.shared.di

import android.content.Context
import com.lebedaliv2601.base.network.httpClient.models.domain.NetworkEnvironment
import com.lebedaliv2601.base.platform.Platform
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