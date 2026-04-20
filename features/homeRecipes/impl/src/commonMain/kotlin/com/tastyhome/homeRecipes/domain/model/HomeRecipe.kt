package com.tastyhome.homeRecipes.domain.model

internal data class HomeRecipe(
    val id: String,
    val title: String,
    val cookingTimeMinutes: Int,
    val proteinsGrams: Int,
    val fatsGrams: Int,
    val carbsGrams: Int,
    val calories: Int,
    val imageUrl: String,
)
