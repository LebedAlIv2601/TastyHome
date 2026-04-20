package com.tastyhome.homeRecipes.presentation.homeRecipes.component

import com.arkivanov.decompose.ComponentContext
import com.tastyhome.base.presentation.uiState.UiState
import com.tastyhome.base.presentation.uiState.toUiState
import com.tastyhome.core.navigation.BaseComponent
import com.tastyhome.core.navigation.Router
import com.tastyhome.homeRecipes.domain.usecases.GetHomeRecipesUseCase
import com.tastyhome.homeRecipes.presentation.homeRecipes.component.se.HomeRecipesEvent
import com.tastyhome.homeRecipes.presentation.homeRecipes.component.se.HomeRecipesState
import com.tastyhome.homeRecipes.presentation.homeRecipes.mapper.HomeRecipeModelMapper
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.AssistedFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal interface HomeRecipesComponent {

    val stateFlow: StateFlow<HomeRecipesState>

    fun onUIEvent(event: HomeRecipesEvent)
}

@AssistedInject
internal class DefaultHomeRecipesComponent(
    @Assisted componentContext: ComponentContext,
    private val getHomeRecipesUseCase: GetHomeRecipesUseCase,
    private val homeRecipeModelMapper: HomeRecipeModelMapper,
) : BaseComponent<Router>(
    router = Router { },
    componentContext = componentContext,
), HomeRecipesComponent {

    private val _stateFlow = MutableStateFlow(HomeRecipesState())
    override val stateFlow: StateFlow<HomeRecipesState> = _stateFlow.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        loadRecipes()
    }

    override fun onUIEvent(event: HomeRecipesEvent) {
        when (event) {
            HomeRecipesEvent.RefreshClicked -> loadRecipes()
            HomeRecipesEvent.RetryClicked -> loadRecipes()
        }
    }

    private fun loadRecipes() {
        scope.launch {
            _stateFlow.update { state ->
                state.copy(recipes = UiState.Loading(state.recipes.model))
            }

            val recipesState = getHomeRecipesUseCase()
                .toUiState(
                    isLoading = false,
                    mapper = { recipes -> recipes.map(homeRecipeModelMapper::map) },
                )

            _stateFlow.update { state -> state.copy(recipes = recipesState) }
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(componentContext: ComponentContext): DefaultHomeRecipesComponent
    }
}
