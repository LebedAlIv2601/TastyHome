package com.tastyhome.homeRecipes.presentation.homeRecipes.mapper

import com.tastyhome.homeRecipes.domain.model.HomeRecipe
import com.tastyhome.homeRecipes.presentation.homeRecipes.model.HomeRecipeModel
import dev.zacsweers.metro.Inject

@Inject
internal class HomeRecipeModelMapper {
    fun map(recipe: HomeRecipe): HomeRecipeModel {
        return HomeRecipeModel(
            id = recipe.id,
            title = recipe.title,
            cookingTimeText = "${recipe.cookingTimeMinutes} мин",
            proteinsText = "${recipe.proteinsGrams} г",
            fatsText = "${recipe.fatsGrams} г",
            carbsText = "${recipe.carbsGrams} г",
            caloriesText = "${recipe.calories} ккал",
            imageUrl = recipe.imageUrl,
        )
    }
}
