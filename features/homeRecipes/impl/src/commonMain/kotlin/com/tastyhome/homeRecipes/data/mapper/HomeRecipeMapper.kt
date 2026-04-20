package com.tastyhome.homeRecipes.data.mapper

import com.tastyhome.homeRecipes.data.model.HomeRecipeDTO
import com.tastyhome.homeRecipes.domain.model.HomeRecipe
import dev.zacsweers.metro.Inject

@Inject
internal class HomeRecipeMapper {
    fun toDomain(dto: HomeRecipeDTO): HomeRecipe {
        return HomeRecipe(
            id = dto.id,
            title = dto.title,
            cookingTimeMinutes = dto.cookingTimeMinutes,
            proteinsGrams = dto.proteinsGrams,
            fatsGrams = dto.fatsGrams,
            carbsGrams = dto.carbsGrams,
            calories = dto.calories,
            imageUrl = dto.imageUrl,
        )
    }
}
