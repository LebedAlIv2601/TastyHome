package com.tastyhome.homeRecipes.di

import com.tastyhome.base.network.httpClient.HttpClientBuilder
import com.tastyhome.homeRecipes.api.HomeRecipesFeatureFactory
import com.tastyhome.homeRecipes.presentation.homeRecipes.component.DefaultHomeRecipesComponent
import com.tastyhome.homeRecipes.presentation.homeRecipes.navigation.HomeRecipesFeatureFactoryImpl
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Scope

@Scope
annotation class HomeRecipesScope

@Inject
internal class HomeRecipesParentDependencies(
    val httpClientBuilder: HttpClientBuilder,
)

@DependencyGraph(
    HomeRecipesScope::class,
)
internal interface HomeRecipesGraph {

    fun homeRecipesComponentFactory(): DefaultHomeRecipesComponent.Factory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Includes parentDependencies: HomeRecipesParentDependencies,
        ): HomeRecipesGraph
    }
}

@BindingContainer
abstract class HomeRecipesAppBindings {
    @Binds
    internal abstract val HomeRecipesFeatureFactoryImpl.bind: HomeRecipesFeatureFactory
}
