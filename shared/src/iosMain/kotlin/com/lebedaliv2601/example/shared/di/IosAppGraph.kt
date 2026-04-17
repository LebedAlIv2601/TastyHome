package com.lebedaliv2601.example.shared.di

import com.lebedaliv2601.base.network.httpClient.models.domain.NetworkEnvironment
import com.lebedaliv2601.base.platform.Platform
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory

@DependencyGraph(AppScope::class, bindingContainers = [SharedBindings::class, StorageBindings::class])
interface IosAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides platform: Platform,
            @Provides environment: NetworkEnvironment,
        ): IosAppGraph
    }
}

fun createRootGraph(platform: Platform, environment: NetworkEnvironment): IosAppGraph {
    return createGraphFactory<IosAppGraph.Factory>().create(platform, environment)
}