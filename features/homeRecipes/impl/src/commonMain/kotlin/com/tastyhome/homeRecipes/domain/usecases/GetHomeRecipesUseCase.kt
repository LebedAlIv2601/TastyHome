package com.tastyhome.homeRecipes.domain.usecases

import com.tastyhome.base.domain.resource.Resource
import com.tastyhome.homeRecipes.data.HomeRecipesRepository
import com.tastyhome.homeRecipes.domain.model.HomeRecipe
import dev.zacsweers.metro.Inject

@Inject
internal class GetHomeRecipesUseCase(
    private val repository: HomeRecipesRepository,
) {
    suspend operator fun invoke(): Resource<List<HomeRecipe>> {
        return repository.getRecipes()
    }
}
