package com.tastyhome.homeRecipes.presentation.homeRecipes.mapper

import com.tastyhome.homeRecipes.domain.model.HomeRecipe
import com.tastyhome.homeRecipes.presentation.homeRecipes.model.HomeRecipeModel
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeRecipeModelMapperTest {

    private val mapper = HomeRecipeModelMapper()

    @Test
    fun mapsDomainToPresentationModelWithFormattedRussianSuffixes() {
        val recipe = HomeRecipe(
            id = "recipe-7",
            title = "Омлет с индейкой",
            cookingTimeMinutes = 20,
            proteinsGrams = 23,
            fatsGrams = 17,
            carbsGrams = 3,
            calories = 265,
            imageUrl = "https://example.com/omelette.jpg",
        )

        val result = mapper.map(recipe)

        assertEquals(
            expected = HomeRecipeModel(
                id = "recipe-7",
                title = "Омлет с индейкой",
                cookingTimeText = "20 мин",
                proteinsText = "23 г",
                fatsText = "17 г",
                carbsText = "3 г",
                caloriesText = "265 ккал",
                imageUrl = "https://example.com/omelette.jpg",
            ),
            actual = result,
        )
    }
}
