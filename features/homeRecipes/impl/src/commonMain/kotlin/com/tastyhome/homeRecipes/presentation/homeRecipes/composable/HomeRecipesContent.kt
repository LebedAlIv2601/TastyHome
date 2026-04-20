package com.tastyhome.homeRecipes.presentation.homeRecipes.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tastyhome.base.presentation.error.EmptyDataUiError
import com.tastyhome.base.presentation.error.NoInternetUiError
import com.tastyhome.base.presentation.error.UiError
import com.tastyhome.base.presentation.uiState.UiState
import com.tastyhome.core.designSystem.theme.MyTheme
import com.tastyhome.homeRecipes.presentation.homeRecipes.component.HomeRecipesComponent
import com.tastyhome.homeRecipes.presentation.homeRecipes.component.se.HomeRecipesEvent
import com.tastyhome.homeRecipes.presentation.homeRecipes.component.se.HomeRecipesState
import com.tastyhome.homeRecipes.presentation.homeRecipes.model.HomeRecipeModel

@Composable
internal fun HomeRecipesContent(
    component: HomeRecipesComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.stateFlow.collectAsState()
    HomeRecipesScreen(
        state = state,
        onEvent = component::onUIEvent,
        modifier = modifier,
    )
}

@Composable
private fun HomeRecipesScreen(
    state: HomeRecipesState,
    onEvent: (HomeRecipesEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MyTheme.colors.bg,
            MyTheme.colors.primary.copy(alpha = 0.35f),
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Домашние рецепты",
            style = MyTheme.typography.h1,
            fontWeight = FontWeight.SemiBold,
            color = MyTheme.colors.stroke,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Быстрый список с КБЖУ и временем приготовления",
            style = MyTheme.typography.caption,
            color = MyTheme.colors.stroke.copy(alpha = 0.75f),
        )
        Spacer(modifier = Modifier.height(14.dp))

        when (val recipesState = state.recipes) {
            is UiState.Loading -> {
                recipesState.model?.let { recipes ->
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MyTheme.colors.primary,
                        trackColor = Color.White.copy(alpha = 0.4f),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RecipesList(recipes = recipes)
                } ?: LoadingState()
            }

            is UiState.Error -> {
                recipesState.model?.let { recipes ->
                    RecipesList(recipes = recipes)
                } ?: ErrorState(
                    error = recipesState.error,
                    onRetry = { onEvent(HomeRecipesEvent.RetryClicked) },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is UiState.Success -> {
                RecipesList(recipes = recipesState.model)
            }
        }
    }
}

@Composable
private fun RecipesList(
    recipes: List<HomeRecipeModel>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = recipes,
            key = { recipe -> recipe.id },
        ) { recipe ->
            HomeRecipeCard(recipe = recipe)
        }
    }
}

@Composable
private fun HomeRecipeCard(
    recipe: HomeRecipeModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f),
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MyTheme.colors.stroke.copy(alpha = 0.12f),
        ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = "Изображение блюда ${recipe.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                        ),
                    ),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = recipe.title,
                    style = MyTheme.typography.body,
                    color = MyTheme.colors.stroke,
                    fontWeight = FontWeight.Medium,
                )
                RecipeMetaRow(
                    title = "Время",
                    value = recipe.cookingTimeText,
                )
                RecipeMetaRow(
                    title = "Белки",
                    value = recipe.proteinsText,
                )
                RecipeMetaRow(
                    title = "Жиры",
                    value = recipe.fatsText,
                )
                RecipeMetaRow(
                    title = "Углеводы",
                    value = recipe.carbsText,
                )
                RecipeMetaRow(
                    title = "Калории",
                    value = recipe.caloriesText,
                )
            }
        }
    }
}

@Composable
private fun RecipeMetaRow(
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MyTheme.typography.caption,
            color = MyTheme.colors.stroke.copy(alpha = 0.75f),
        )
        Text(
            text = value,
            style = MyTheme.typography.caption,
            color = MyTheme.colors.stroke,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MyTheme.colors.primary)
    }
}

@Composable
private fun ErrorState(
    error: UiError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = when (error) {
                    is NoInternetUiError -> "Проблема с интернетом. Проверьте подключение."
                    is EmptyDataUiError -> "Рецепты пока не найдены."
                    else -> "Не удалось загрузить рецепты."
                },
                style = MyTheme.typography.body,
                color = MyTheme.colors.stroke,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyTheme.colors.primary,
                    contentColor = MyTheme.colors.stroke,
                ),
            ) {
                Text(text = "Повторить")
            }
        }
    }
}
