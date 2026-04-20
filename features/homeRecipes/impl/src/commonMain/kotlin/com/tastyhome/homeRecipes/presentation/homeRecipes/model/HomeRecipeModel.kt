package com.tastyhome.homeRecipes.presentation.homeRecipes.model

internal data class HomeRecipeModel(
    val id: String,
    val title: String,
    val cookingTimeText: String,
    val proteinsText: String,
    val fatsText: String,
    val carbsText: String,
    val caloriesText: String,
    val imageUrl: String,
)
