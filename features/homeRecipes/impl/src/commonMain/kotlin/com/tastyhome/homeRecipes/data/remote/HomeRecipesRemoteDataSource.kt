package com.tastyhome.homeRecipes.data.remote

import com.tastyhome.base.network.httpClient.HttpClientBuilder
import com.tastyhome.homeRecipes.data.model.HomeRecipeDTO
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.delay

@Inject
internal class HomeRecipesRemoteDataSource(
    private val httpClientBuilder: HttpClientBuilder,
) {
    private val networkEnvironmentKey: String = httpClientBuilder.environment.key

    suspend fun getRecipes(): List<HomeRecipeDTO> {
        // Пока сервер не готов, возвращаем мок-ответ как сетевой источник.
        delay(
            if (networkEnvironmentKey.isBlank()) 300L else 350L,
        )
        return mockRecipes
    }

    private val mockRecipes: List<HomeRecipeDTO> = listOf(
        HomeRecipeDTO(
            id = "1",
            title = "Куриные котлеты с овощами",
            cookingTimeMinutes = 35,
            proteinsGrams = 24,
            fatsGrams = 12,
            carbsGrams = 9,
            calories = 245,
            imageUrl = "https://picsum.photos/id/292/800/500",
        ),
        HomeRecipeDTO(
            id = "2",
            title = "Паста с томатным соусом и базиликом",
            cookingTimeMinutes = 30,
            proteinsGrams = 11,
            fatsGrams = 10,
            carbsGrams = 55,
            calories = 410,
            imageUrl = "https://picsum.photos/id/1080/800/500",
        ),
        HomeRecipeDTO(
            id = "3",
            title = "Запеченная рыба с лимоном",
            cookingTimeMinutes = 40,
            proteinsGrams = 29,
            fatsGrams = 14,
            carbsGrams = 4,
            calories = 285,
            imageUrl = "https://picsum.photos/id/102/800/500",
        ),
        HomeRecipeDTO(
            id = "4",
            title = "Тушеная говядина с гречкой",
            cookingTimeMinutes = 50,
            proteinsGrams = 33,
            fatsGrams = 16,
            carbsGrams = 31,
            calories = 490,
            imageUrl = "https://picsum.photos/id/659/800/500",
        ),
        HomeRecipeDTO(
            id = "5",
            title = "Овощной крем-суп",
            cookingTimeMinutes = 25,
            proteinsGrams = 6,
            fatsGrams = 8,
            carbsGrams = 18,
            calories = 170,
            imageUrl = "https://picsum.photos/id/429/800/500",
        ),
        HomeRecipeDTO(
            id = "6",
            title = "Сырники в духовке",
            cookingTimeMinutes = 30,
            proteinsGrams = 19,
            fatsGrams = 11,
            carbsGrams = 22,
            calories = 285,
            imageUrl = "https://picsum.photos/id/431/800/500",
        ),
        HomeRecipeDTO(
            id = "7",
            title = "Омлет с индейкой и зеленью",
            cookingTimeMinutes = 20,
            proteinsGrams = 23,
            fatsGrams = 17,
            carbsGrams = 3,
            calories = 265,
            imageUrl = "https://picsum.photos/id/312/800/500",
        ),
        HomeRecipeDTO(
            id = "8",
            title = "Салат с киноа и авокадо",
            cookingTimeMinutes = 18,
            proteinsGrams = 9,
            fatsGrams = 15,
            carbsGrams = 27,
            calories = 320,
            imageUrl = "https://picsum.photos/id/292/800/501",
        ),
        HomeRecipeDTO(
            id = "9",
            title = "Тефтели в томатном соусе",
            cookingTimeMinutes = 45,
            proteinsGrams = 27,
            fatsGrams = 13,
            carbsGrams = 17,
            calories = 355,
            imageUrl = "https://picsum.photos/id/888/800/500",
        ),
        HomeRecipeDTO(
            id = "10",
            title = "Ленивые вареники",
            cookingTimeMinutes = 22,
            proteinsGrams = 16,
            fatsGrams = 7,
            carbsGrams = 39,
            calories = 305,
            imageUrl = "https://picsum.photos/id/1060/800/500",
        ),
    )
}
