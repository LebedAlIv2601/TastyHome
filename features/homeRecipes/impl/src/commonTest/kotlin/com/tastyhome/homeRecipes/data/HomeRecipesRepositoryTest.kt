package com.tastyhome.homeRecipes.data

import com.tastyhome.base.domain.resource.Resource
import com.tastyhome.base.network.httpClient.HttpClientBuilder
import com.tastyhome.base.network.httpClient.HttpClientSetting
import com.tastyhome.base.network.httpClient.models.domain.NetworkEnvironment
import com.tastyhome.homeRecipes.data.mapper.HomeRecipeMapper
import com.tastyhome.homeRecipes.data.remote.HomeRecipesRemoteDataSource
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class HomeRecipesRepositoryTest {

    @Test
    fun getRecipesReturnsSuccessWithMappedRecipes() = runBlocking {
        val repository = HomeRecipesRepository(
            remoteDataSource = HomeRecipesRemoteDataSource(StubHttpClientBuilder()),
            homeRecipeMapper = HomeRecipeMapper(),
        )

        val result = repository.getRecipes()

        when (result) {
            is Resource.Success -> {
                val recipes = result.value
                assertTrue(recipes.isNotEmpty())
                assertEquals("1", recipes.first().id)
                assertEquals("Куриные котлеты с овощами", recipes.first().title)
            }

            is Resource.Error -> fail("Expected successful recipes response, got ${result.error}")
        }
    }

    private class StubHttpClientBuilder : HttpClientBuilder {
        override val environment: NetworkEnvironment = StubNetworkEnvironment
        override val settings: MutableList<HttpClientSetting> = mutableListOf()
    }

    private object StubNetworkEnvironment : NetworkEnvironment {
        override val key: String = ""
    }
}
