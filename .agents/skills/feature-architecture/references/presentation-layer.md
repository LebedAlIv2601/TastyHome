# Presentation Layer

## Purpose

Presentation layer отвечает за экранное поведение фичи:

- получает observable domain data и statuses из domain/usecase слоя;
- преобразует локальные данные, loading flag и `ResultStatus` в `UiState<PresentationModel>`;
- хранит единое состояние экрана в Decompose component;
- принимает пользовательские намерения из Compose UI через единый event-entrypoint;
- отрисовывает UI через Compose без доступа к data/domain implementation details.

Presentation слой не должен протаскивать в UI DTO, Entity, Repository, `DataError` или `Throwable`. До composable должны доходить только `State`, `Event`, presentation models, `UiState<T>` и `UiError`.

## Structure

Для каждого отдельного экрана или component subtree создавай отдельную папку внутри `presentation/`:

```text
presentation/
└── profile/
    ├── component/
    │   ├── ProfileComponent.kt
    │   ├── ProfileEvent.kt
    │   └── ProfileState.kt
    ├── composable/
    │   ├── ProfileContent.kt
    │   └── ProfileScreen.kt
    ├── mapper/
    │   └── ProfileModelMapper.kt
    ├── model/
    │   └── ProfileModel.kt
    └── navigation/
        └── ProfileFeatureFactoryImpl.kt
```

Правила:

- `navigation/` создает feature entrypoint и связывает component с top-level composable.
- `component/` содержит concrete Decompose component class и его assisted factory.
- `component/` также содержит `*State` и `*Event` для взаимодействия component и UI.
- `composable/` содержит Compose функции экрана и мелкие UI-блоки.
- `model/` содержит presentation models, если domain model нельзя отдавать в UI напрямую.
- `mapper/` содержит `*ModelMapper`, которые маппят domain models в presentation models.
- Не создавай пустые папки. Если экрану не нужны presentation models или mapper'ы, папки `model/` и `mapper/` не нужны.

## Presentation Models

Presentation model описывает данные в форме, удобной конкретному экрану. Это не копия DTO/Entity и не публичная API model.

```kotlin
@Immutable
internal data class ProfileModel(
    val id: String,
    val title: String,
    val subtitle: String?,
    val avatarUrl: String?,
    val isEditable: Boolean,
)
```

Правила:

- Модели лежат в `presentation/<screen>/model` и называются `*Model`.
- Модели immutable: используй `data class` с `val`.
- Все presentation models, которые используются в UI, должны быть помечены Compose-аннотацией `@Immutable`.
- Если другая модель presentation-слоя напрямую передается в composable или лежит внутри `UiState<...>` для composable, она тоже должна быть помечена `@Immutable`.
- Модели должны быть `internal`, если нет явной причины расширять видимость.
- Модели не должны содержать DTO, Entity, `UiState`, `Throwable`, `DataError`, `CoroutineScope`, Decompose или Compose component types.
- В модель можно класть UI-ready поля: отформатированные строки, флаги доступности actions, сгруппированные элементы списка.
- Не клади в presentation model callbacks и mutable state. Действия идут через `Event`.
- Если domain model уже идеально подходит экрану и не протекает через public API, отдельная presentation model не обязательна. Но как только появляются форматирование, группировка, UI flags или несколько domain-моделей, создавай `*Model`.

## Model Mappers

Mapper presentation слоя преобразует domain model в presentation model. Он не должен ходить в сеть/БД, запускать use cases или менять state component.

```kotlin
internal class ProfileModelMapper @Inject constructor() {

    fun map(profile: Profile): ProfileModel {
        return ProfileModel(
            id = profile.id,
            title = profile.name,
            subtitle = profile.description,
            avatarUrl = profile.avatarUrl,
            isEditable = profile.permissions.canEdit,
        )
    }
}
```

Правила:

- Mapper лежит в `presentation/<screen>/mapper` и называется `*ModelMapper`.
- Вход mapper'а: domain model или модель из api модуля фичи, если она является domain-level contract.
- Выход mapper'а: presentation `*Model`.
- Если mapper возвращает presentation model для UI, целевая модель обязана быть `@Immutable`.
- Mapper не возвращает `UiState`; `UiState` создается через функции из `base/presentation`.
- Если mapper нужен только в одном component, инжектируй его в component через Metro.

## State And Events

Component взаимодействует с UI через два публичных канала:

- единый `StateFlow<ScreenState>` с полным состоянием экрана;
- единый метод `onUIEvent(event)` для всех событий от UI.

```kotlin
internal data class RecipesState(
    val recipes: UiState<List<RecipeModel>> = UiState.Loading(),
    val filters: RecipeFilters = RecipeFilters(),
    val cookingTimeDraft: Int? = null,
)
```

```kotlin
internal sealed interface RecipesEvent {
    data object RetryClicked : RecipesEvent
    data class CookingTimeChanged(val minutes: Int?) : RecipesEvent
    data object FiltersApplied : RecipesEvent
}
```

Правила для state:

- State лежит рядом с component в `component/` и называется `*State`.
- State должен быть одним `data class` на component.
- State содержит все, что нужно экрану для стабильной отрисовки: данные через `UiState`, input values, selected ids/tabs, dialog flags, validation flags.
- State component хранит UI/process state: transient loading, draft input values, dialog/sheet visibility, selected UI tab, validation flags, enabled/disabled flags, one-screen visual state.
- State component не является владельцем примененного data/query state, который меняет состав основных данных. Такие фильтры, сортировки, search query и paging/query params хранятся в repository и приходят в component через use case flow.
- State должен иметь безопасные default values, чтобы UI мог отрисоваться до первой загрузки.
- Для экранных данных используй `UiState<PresentationModel>` или `UiState<List<PresentationModel>>`.
- Итоговый `state` должен собираться реактивно из нескольких input-flow через `combine(...).stateIn(...)`.
- `MutableStateFlow` внутри component допустим только как private input-flow для сборки state, а не как вручную поддерживаемый полный `ScreenState`.
- Сам `state` нельзя менять руками через `_stateFlow.update { ... }`; меняются только входные flow, из которых этот state собран.
- Не дроби состояние на несколько публичных flow. Если UI должен знать значение, оно должно попасть в единый `ScreenState`.
- Не клади в state `Throwable`, `DataError`, use cases, repositories, mutable collections или callbacks.

Правила для events:

- Events лежат рядом с component в `component/` и называются `*Event`.
- Используй `sealed interface`.
- Event описывает намерение пользователя или UI lifecycle trigger: `RetryClicked`, `SearchQueryChanged`, `RefreshTriggered`, `BackClicked`.
- Event не должен описывать implementation detail component: не `LoadProfileUseCaseStarted`, не `RepositoryFailed`.
- Для value changes используй `data class`.
- Для кликов/action без payload используй `data object`.
- Не создавай отдельные методы component вроде `onBackClick`, `onRetryClick`, `onNameChanged`. Все события идут через `onUIEvent(event)`.

## Screen Data State Pattern

Для данных, которые отображаются на экране, component собирает `UiState` из трех источников:

```text
observable local data + UI loading flag + last query ResultStatus -> UiState
```

Правила:

- `observe*UseCase()` возвращает локальные observable domain данные: `Flow<T?>`, `Flow<List<T>?>` или другой flow без UI-моделей.
- Если на наполнение данных влияют примененные фильтры/query, они хранятся в repository как domain data/query state и участвуют в `observe*UseCase()` или отдельном `observe*FiltersUseCase()`.
- Component отправляет примененные изменения фильтров целиком через use case вроде `Update*FiltersUseCase(filters)`.
- Component может хранить draft-значение UI-контрола локально, если оно еще не должно менять data query. После применения draft конвертируется в domain filters/query и отправляется в repository через use case.
- Если domain filters/query неудобны для UI напрямую, component маппит их в presentation model перед добавлением в `ScreenState`.
- `refresh*UseCase()` возвращает `ResultStatus`.
- Component хранит private `MutableStateFlow<Boolean>` для transient loading.
- Component хранит private `MutableStateFlow<ResultStatus>` или nullable status flow для последнего результата запроса.
- `createUiState(data, isLoading, status, mapper = ...)` вызывается в component/presentation layer.
- Initial state должен быть `UiState.Loading()`, а не error flicker до первого request.
- Loading flag остается UI-логикой component и не переносится в repository/use case только ради loader.

## Component Contract

Screen component является presentation controller: управляет lifecycle, вызывает use cases, маппит результаты, обновляет input-flow и дергает router/callbacks. Он не содержит Compose UI.

```kotlin
@AssistedInject
internal class RecipesComponent(
    @Assisted componentContext: ComponentContext,
    private val observeRecipesUseCase: ObserveRecipesUseCase,
    private val observeRecipeFiltersUseCase: ObserveRecipeFiltersUseCase,
    private val updateRecipeFiltersUseCase: UpdateRecipeFiltersUseCase,
    private val refreshRecipesUseCase: RefreshRecipesUseCase,
    private val recipeModelMapper: RecipeModelMapper,
) : BaseComponent<Router>(
    router = Router { },
    componentContext = componentContext,
) {
    private val loadingFlow = MutableStateFlow(false)
    private val cookingTimeDraftFlow = MutableStateFlow<Int?>(null)
    private val queryStatusFlow = MutableStateFlow<ResultStatus>(
        ResultStatus.Error(emptyDataError())
    )
    private val filtersFlow = observeRecipeFiltersUseCase()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecipeFilters(),
        )

    private val recipesUiFlow = combine(
        observeRecipesUseCase(),
        loadingFlow,
        queryStatusFlow,
    ) { recipes, loading, status ->
        createUiState(recipes, loading, status) { items ->
            items.map(recipeModelMapper::map)
        }
    }

    val state: StateFlow<RecipesState> = combine(
        recipesUiFlow,
        filtersFlow,
        cookingTimeDraftFlow,
    ) { recipes, filters, cookingTimeDraft ->
        RecipesState(
            recipes = recipes,
            filters = filters,
            cookingTimeDraft = cookingTimeDraft,
        )
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecipesState(),
        )

    override fun onCreate() {
        super.onCreate()
        refresh()
    }

    fun onUIEvent(event: RecipesEvent) {
        when (event) {
            RecipesEvent.RetryClicked -> refresh()
            is RecipesEvent.CookingTimeChanged -> {
                cookingTimeDraftFlow.value = event.minutes
            }
            RecipesEvent.FiltersApplied -> {
                updateRecipeFiltersUseCase(
                    filtersFlow.value.copy(
                        maxCookingTimeMinutes = cookingTimeDraftFlow.value,
                    )
                )
            }
        }
    }

    private fun refresh() {
        scope.doWithLoading(loadingFlow) {
            queryStatusFlow.update { refreshRecipesUseCase() }
        }
    }
}
```

Правила:

- Runtime параметры component помечай `@Assisted`: `ComponentContext`, args/config fields, router/callbacks.
- DI зависимости передавай обычными constructor параметрами.
- По умолчанию не создавай отдельную пару `interface + Default...Component`. Для screen component используй один concrete класс `*Component`.
- Публично expose только `StateFlow`, не `MutableStateFlow`.
- Публичное свойство состояния называй `state`.
- Собирай итоговый `state` реактивно через `combine(...).stateIn(...)` из private input-flow и domain/usecase flow.
- Private `MutableStateFlow` используй только как input-flow для state assembly.
- Не поддерживай основной `ScreenState` вручную через `MutableStateFlow<ScreenState>` и `_stateFlow.update { ... }`.
- Все UI события обрабатывай в одном `onUIEvent(event)` через exhaustive `when`.
- Component может вызывать router/callbacks, use cases, mapper'ы и platform abstractions.
- Component отвечает только за подготовку данных к отображению и реакцию на UI events.
- Component не владеет примененными фильтрами/query, которые меняют данные. Read-only `StateFlow`, полученный из use case через `stateIn`, допустим как локальный snapshot для сборки `ScreenState` и обработки event.
- Component может хранить draft UI input до применения.
- Если component поднимает временный UI loading flag перед `scope.launch { ... }`, предпочитай поднимать флаг снаружи launch и сбрасывать его через `job.invokeOnCompletion { ... }` или общий helper вроде `doWithLoading`.
- Component не должен реализовывать бизнес-логику, persistent caching, source-of-truth decisions, retry strategy уровня data/domain или reconciliation данных из нескольких источников.
- Component не должен импортировать Compose, UI modifiers, DTO, Entity или concrete data sources.
- Component не должен реализовывать `Feature`; `Feature` создается в `navigation/`.
- One-shot effect flow не создавай. Навигацию обрабатывай в component через router/callbacks, а отображаемые сообщения/диалоги моделируй в едином state.

## Result To UiState

Экранные данные приходят из observable local source, а статус последнего request приходит как `ResultStatus`.

Presentation слой преобразует это в `UiState<PresentationModel>` функцией из `base/presentation`:

```kotlin
val uiState = createUiState(
    data = recipes,
    isLoading = loading,
    status = status,
) { items ->
    items.map(recipeModelMapper::map)
}
```

`createUiState(...)`:

- при `isLoading = true` возвращает `UiState.Loading`, сохраняя mapped model, если data уже есть;
- при успешном status и non-null data возвращает `UiState.Success(mappedValue)`;
- при error status возвращает `UiState.Error(errorMapper(error), mappedCachedValue)`;
- при отсутствующих data/status возвращает empty-data UI error;
- по умолчанию маппит ошибку через `Throwable.toUiError()`.

Правила:

- Не создавай `UiState` вручную, если подходит `createUiState(...)`.
- Передавай в `mapper` domain-to-presentation mapper: `profileModelMapper::map`.
- Если нужно преобразовать уже готовый `UiState<T>` в `UiState<R>`, используй `UiState.map { ... }` из `base/presentation`.
- Для side effects при смене состояния можно использовать `onLoading`, `onSuccess`, `onError` из `base/presentation`, но они не заменяют state modeling.
- Не отдавай `ResultStatus`, `Result<T>`, `Throwable` или data errors в composable.

## UiError Mapping

`UiError` является единственной моделью ошибки для UI. `DataError` и `Throwable` должны быть преобразованы до попадания в composable.

Базовые типы из `base/presentation`:

- `NoInternetUiError`
- `EmptyDataUiError`
- `UnknownUiError`

Базовый mapper:

```kotlin
fun Throwable.toUiError(): UiError
fun DataError.toUiError(): UiError
```

Правила:

- `Throwable.toUiError()` маппит `DataError` через `DataError.toUiError()`, остальные исключения в `UnknownUiError`.
- `DataError.toUiError()` маппит `INTERNET_CONNECTION_ERROR_CODE` в `NoInternetUiError`, `EMPTY_DATA_ERROR_CODE` в `EmptyDataUiError`, остальные коды в `UnknownUiError`.
- Expected ошибки data/domain слоя должны приходить как `DataError`, чтобы UI получил понятный `UiError`.
- Unexpected exception не показывай в UI напрямую; он должен стать `UnknownUiError`, а детали при необходимости логируются отдельно.
- Если фиче нужны специальные ошибки, добавь feature-specific `UiError` в presentation layer и передай custom `errorMapper` в `createUiState(...)`.
- Composable делает rendering по `UiError`, но не знает про `DataError.code`, exception classes или network implementation.

## Composable Rules

Composable слой делится на connector и pure screen:

- `*Content` принимает `component`, собирает `state` и передает state/events дальше.
- `*Screen` принимает `state`, `onEvent` и `modifier`, не знает про Decompose component.
- Мелкие composable принимают конкретные values и callbacks/events, а не весь component.

```kotlin
@Composable
internal fun ProfileContent(
    component: ProfileComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsStateWithLifecycle()

    ProfileScreen(
        state = state,
        onEvent = component::onUIEvent,
        modifier = modifier,
    )
}
```

```kotlin
@Composable
internal fun ProfileScreen(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val profileState = state.profile) {
        is UiState.Loading -> ProfileLoading(
            model = profileState.model,
            modifier = modifier,
        )
        is UiState.Error -> ProfileError(
            error = profileState.error,
            model = profileState.model,
            onRetryClick = { onEvent(ProfileEvent.RetryClicked) },
            modifier = modifier,
        )
        is UiState.Success -> ProfileData(
            model = profileState.model,
            onBackClick = { onEvent(ProfileEvent.BackClicked) },
            modifier = modifier,
        )
    }
}
```

Правила:

- UI отправляет наверх только `Event`, а не вызывает use cases, repositories или router напрямую.
- `*Screen` и дочерние composable должны быть максимально pure: state in, event out.
- Не собирай несколько flow из component в composable. Собирай единый `state`.
- Используй design system (`MyTheme`, базовые components, typography/colors) вместо локальных случайных цветов/типографики.
- Не держи business logic в composable. Сложные вычисления и форматирование выноси в mapper/component.
- Для platform behavior используй platform abstractions через component/usecase, а не прямые platform calls из UI.
- Preview/test composable должны работать через fake `State`, без настоящего component и DI.

## Anti-Patterns

- `fun onRetryClick()` / `fun onBackClick()` / `fun onQueryChanged(value: String)` в component class вместо `onUIEvent(event)`.
- Несколько публичных `StateFlow` под разные куски экрана.
- Примененные фильтры, сортировки или search query хранятся в component, хотя они меняют состав основных данных.
- Draft UI input хранится в repository, хотя он еще не применен к data query.
- `Throwable` или `DataError` в `*State`.
- DTO/Entity в presentation model или composable.
- Mapper, который возвращает `UiState`.
- Composable, который принимает repository/usecase/component factory.
- Component с `@Composable fun View()`.
- Public feature api, который раскрывает presentation `State`, `Event`, component или `UiState`.
