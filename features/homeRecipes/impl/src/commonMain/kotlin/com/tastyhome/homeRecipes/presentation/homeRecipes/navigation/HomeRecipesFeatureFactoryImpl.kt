package com.tastyhome.homeRecipes.presentation.homeRecipes.navigation

import com.arkivanov.decompose.ComponentContext
import com.tastyhome.core.navigation.api.EmptyArgs
import com.tastyhome.core.navigation.api.Feature
import com.tastyhome.core.navigation.api.NoCallbacks
import com.tastyhome.homeRecipes.api.HomeRecipesFeatureFactory
import com.tastyhome.homeRecipes.di.HomeRecipesGraph
import com.tastyhome.homeRecipes.di.HomeRecipesParentDependencies
import com.tastyhome.homeRecipes.presentation.homeRecipes.composable.HomeRecipesContent
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraphFactory

@Inject
internal class HomeRecipesFeatureFactoryImpl(
    private val parentDependencies: HomeRecipesParentDependencies,
) : HomeRecipesFeatureFactory {

    override fun create(
        componentContext: ComponentContext,
        args: EmptyArgs,
        callbacks: NoCallbacks,
    ): Feature {
        val graph = createGraphFactory<HomeRecipesGraph.Factory>()
            .create(parentDependencies)

        val component = graph.homeRecipesComponentFactory()
            .create(componentContext = componentContext)

        return Feature { modifier ->
            HomeRecipesContent(
                component = component,
                modifier = modifier,
            )
        }
    }
}
