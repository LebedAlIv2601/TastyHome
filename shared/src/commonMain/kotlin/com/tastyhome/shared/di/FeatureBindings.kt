package com.tastyhome.shared.di

import com.tastyhome.homeRecipes.di.HomeRecipesAppBindings
import dev.zacsweers.metro.BindingContainer

@BindingContainer(
    includes = [
        HomeRecipesAppBindings::class,
    ],
)
internal interface FeatureBindings
