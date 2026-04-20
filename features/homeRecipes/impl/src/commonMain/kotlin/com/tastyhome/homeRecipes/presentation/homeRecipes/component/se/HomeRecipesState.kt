package com.tastyhome.homeRecipes.presentation.homeRecipes.component.se

import com.tastyhome.base.presentation.uiState.UiState
import com.tastyhome.homeRecipes.presentation.homeRecipes.model.HomeRecipeModel

internal data class HomeRecipesState(
    val recipes: UiState<List<HomeRecipeModel>> = UiState.Loading(),
)
