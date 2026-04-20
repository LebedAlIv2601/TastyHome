package com.tastyhome.homeRecipes.data.model

internal data class HomeRecipeDTO(
    val id: String,
    val title: String,
    val cookingTimeMinutes: Int,
    val proteinsGrams: Int,
    val fatsGrams: Int,
    val carbsGrams: Int,
    val calories: Int,
    val imageUrl: String,
)
