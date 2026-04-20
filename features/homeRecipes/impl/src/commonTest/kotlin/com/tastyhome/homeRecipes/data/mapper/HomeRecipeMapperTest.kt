package com.tastyhome.homeRecipes.data.mapper

import com.tastyhome.homeRecipes.data.model.HomeRecipeDTO
import com.tastyhome.homeRecipes.domain.model.HomeRecipe
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeRecipeMapperTest {

    private val mapper = HomeRecipeMapper()

    @Test
    fun mapsDtoToDomainWithAllFields() {
        val dto = HomeRecipeDTO(
            id = "recipe-42",
            title = "Гречка с грибами",
            cookingTimeMinutes = 27,
            proteinsGrams = 13,
            fatsGrams = 8,
            carbsGrams = 45,
            calories = 310,
            imageUrl = "https://example.com/recipe.jpg",
        )

        val result = mapper.toDomain(dto)

        assertEquals(
            expected = HomeRecipe(
                id = "recipe-42",
                title = "Гречка с грибами",
                cookingTimeMinutes = 27,
                proteinsGrams = 13,
                fatsGrams = 8,
                carbsGrams = 45,
                calories = 310,
                imageUrl = "https://example.com/recipe.jpg",
            ),
            actual = result,
        )
    }
}
