package com.tastyhome.homeRecipes.data

import com.tastyhome.base.domain.resource.Resource
import com.tastyhome.base.domain.resource.utils.runCatchingResource
import com.tastyhome.base.network.fetch.fetch
import com.tastyhome.homeRecipes.data.mapper.HomeRecipeMapper
import com.tastyhome.homeRecipes.data.remote.HomeRecipesRemoteDataSource
import com.tastyhome.homeRecipes.domain.model.HomeRecipe
import dev.zacsweers.metro.Inject

@Inject
internal class HomeRecipesRepository(
    private val remoteDataSource: HomeRecipesRemoteDataSource,
    private val homeRecipeMapper: HomeRecipeMapper,
) {
    suspend fun getRecipes(): Resource<List<HomeRecipe>> {
        return runCatchingResource {
            fetch { remoteDataSource.getRecipes() }
                .map(homeRecipeMapper::toDomain)
        }
    }
}
