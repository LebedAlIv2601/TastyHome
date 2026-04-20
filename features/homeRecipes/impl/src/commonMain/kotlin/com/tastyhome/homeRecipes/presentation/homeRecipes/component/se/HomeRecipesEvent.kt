package com.tastyhome.homeRecipes.presentation.homeRecipes.component.se

internal sealed interface HomeRecipesEvent {
    data object RetryClicked : HomeRecipesEvent
    data object RefreshClicked : HomeRecipesEvent
}
