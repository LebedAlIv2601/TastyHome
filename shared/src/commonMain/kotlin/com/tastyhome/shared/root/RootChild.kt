package com.tastyhome.shared.root

import com.tastyhome.core.navigation.api.Feature
import kotlinx.serialization.Serializable

sealed interface RootChild : Feature {
    class HomeRecipesChild(
        private val feature: Feature,
    ) : RootChild, Feature by feature
}

@Serializable
sealed class Config {
    @Serializable
    data object HomeRecipes : Config()
}
