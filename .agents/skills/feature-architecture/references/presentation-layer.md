# Presentation Layer

## Purpose

Presentation layer отвечает за экранное поведение фичи:

- получает данные из domain/usecase слоя;
- преобразует domain `Resource<T>` в `UiState<PresentationModel>`;
- хранит единое состояние экрана в Decompose component;
- принимает пользовательские намерения из Compose UI через единый event-entrypoint;
- отрисовывает UI через Compose без доступа к data/domain implementation details.

Presentation слой не должен протаскивать в UI DTO, Entity, Repository, `Resource<T>`, `DataError` или `Throwable`. До composable должны доходить только `State`, `Event`, presentation models, `UiState<T>` и `UiError`.

## Structure

Для каждого отдельного экрана или component subtree создавай отдельную папку внутри `presentation/`:

```text
presentation/
└── profile/
    ├── component/
    │   ├── ProfileComponent.kt
    │   └── s&e/
    │       ├── ProfileEvent.kt
    │       └── ProfileState.kt
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
- `component/` содержит Decompose component interface, default implementation и assisted factory.
- `component/s&e/` содержит state и events для взаимодействия component и UI.
- `composable/` содержит Compose функции экрана и мелкие UI-блоки.
- `model/` содержит presentation models, если domain model нельзя отдавать в UI напрямую.
- `mapper/` содержит `*ModelMapper`, которые маппят domain models в presentation models.
- Не создавай пустые папки. Если экрану не нужны presentation models или mapper'ы, папки `model/` и `mapper/` не нужны.

## Presentation Models

Presentation model описывает данные в форме, удобной конкретному экрану. Это не копия DTO/Entity и не публичная API model.

```kotlin
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
- Модели должны быть `internal`, если нет явной причины расширять видимость.
- Модели не должны содержать DTO, Entity, `Resource`, `UiState`, `Throwable`, `DataError`, `CoroutineScope`, Decompose или Compose component types.
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
- Mapper не возвращает `UiState`; `UiState` создается через функции из `base/presentation`.
- Если mapper нужен только в одном component, инжектируй его в component через Metro.

## State And Events

Component взаимодействует с UI через два публичных канала:

- единый `StateFlow<ScreenState>` с полным состоянием экрана;
- единый метод `onUIEvent(event)` для всех событий от UI.

```kotlin
internal data class ProfileState(
    val profile: UiState<ProfileModel> = UiState.Loading(),
    val isRefreshing: Boolean = false,
    val selectedTab: ProfileTab = ProfileTab.Info,
)
```

```kotlin
internal sealed interface ProfileEvent {
    data object BackClicked : ProfileEvent
    data object RetryClicked : ProfileEvent
    data object RefreshTriggered : ProfileEvent
    data class TabSelected(val tab: ProfileTab) : ProfileEvent
}
```

Правила для state:

- State лежит в `component/s&e` и называется `*State`.
- State должен быть одним `data class` на component.
- State содержит все, что нужно экрану для стабильной отрисовки: данные, loading/error через `UiState`, input values, selected ids/tabs, dialog flags, validation flags.
- State должен иметь безопасные default values, чтобы UI мог отрисоваться до первой загрузки.
- Для данных, пришедших из domain/usecase, используй `UiState<PresentationModel>` или `UiState<List<PresentationModel>>`.
- Не дроби состояние на несколько публичных flow. Если UI должен знать значение, оно должно попасть в единый `ScreenState`.
- Не клади в state `Resource`, `Throwable`, `DataError`, use cases, repositories, mutable collections или callbacks.

Правила для events:

- Events лежат в `component/s&e` и называются `*Event`.
- Используй `sealed interface`.
- Event описывает намерение пользователя или UI lifecycle trigger: `RetryClicked`, `SearchQueryChanged`, `RefreshTriggered`, `BackClicked`.
- Event не должен описывать implementation detail component: не `LoadProfileUseCaseStarted`, не `RepositoryFailed`.
- Для value changes используй `data class`.
- Для кликов/action без payload используй `data object`.
- Не создавай отдельные методы component вроде `onBackClick`, `onRetryClick`, `onNameChanged`. Все события идут через `onUIEvent(event)`.

## Component Contract

Screen component является presentation controller: управляет lifecycle, вызывает use cases, маппит результаты, обновляет state и дергает router/callbacks. Он не содержит Compose UI.

```kotlin
internal interface ProfileComponent {

    val stateFlow: StateFlow<ProfileState>

    fun onUIEvent(event: ProfileEvent)

    @AssistedFactory
    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
            profileId: String,
            router: ProfileRouter,
        ): ProfileComponent
    }
}
```

```kotlin
@AssistedInject
internal class DefaultProfileComponent(
    @Assisted componentContext: ComponentContext,
    @Assisted private val profileId: String,
    @Assisted private val router: ProfileRouter,
    private val getProfileUseCase: GetProfileUseCase,
    private val profileModelMapper: ProfileModelMapper,
) : BaseComponent<ProfileRouter>(
    router = router,
    componentContext = componentContext,
), ProfileComponent {

    private val _stateFlow = MutableStateFlow(ProfileState())
    override val stateFlow: StateFlow<ProfileState> = _stateFlow.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        loadProfile()
    }

    override fun onUIEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.BackClicked -> router.goBack()
            ProfileEvent.RetryClicked -> loadProfile()
            ProfileEvent.RefreshTriggered -> refreshProfile()
            is ProfileEvent.TabSelected -> {
                _stateFlow.update { state -> state.copy(selectedTab = event.tab) }
            }
        }
    }

    private fun loadProfile() {
        scope.launch {
            _stateFlow.update { state ->
                state.copy(profile = UiState.Loading(state.profile.model))
            }

            val resource = getProfileUseCase(profileId)
            val profileState = resource.toUiState(
                isLoading = false,
                mapper = profileModelMapper::map,
            )

            _stateFlow.update { state -> state.copy(profile = profileState) }
        }
    }

    private fun refreshProfile() {
        scope.launch {
            _stateFlow.update { state -> state.copy(isRefreshing = true) }
            val resource = getProfileUseCase(profileId)
            val profileState = resource.toUiState(
                isLoading = false,
                mapper = profileModelMapper::map,
            )
            _stateFlow.update { state ->
                state.copy(
                    profile = profileState,
                    isRefreshing = false,
                )
            }
        }
    }
}
```

Правила:

- Runtime параметры component помечай `@Assisted`: `ComponentContext`, args/config fields, router/callbacks.
- DI зависимости передавай обычными constructor параметрами.
- Публично expose только `StateFlow`, не `MutableStateFlow`.
- Обновляй state через `_stateFlow.update { it.copy(...) }`.
- Все UI события обрабатывай в одном `onUIEvent(event)` через exhaustive `when`.
- Component может вызывать router/callbacks, use cases, mapper'ы и platform abstractions.
- Component не должен импортировать Compose, UI modifiers, DTO, Entity или concrete data sources.
- Component не должен реализовывать `Feature`; `Feature` создается в `navigation/`.
- One-shot effect flow не создавай. Навигацию обрабатывай в component через router/callbacks, а отображаемые сообщения/диалоги моделируй в едином state.

## Resource To UiState

Domain слой возвращает `Resource<Domain>` или flow таких ресурсов. Presentation слой преобразует его в `UiState<PresentationModel>` функцией из `base/presentation`:

```kotlin
val uiState: UiState<ProfileModel> = resource.toUiState(
    isLoading = false,
    mapper = profileModelMapper::map,
)
```

`Resource.toUiState(...)`:

- при `isLoading = true` возвращает `UiState.Loading`, сохраняя `resource.value` как mapped cached model, если он есть;
- при `Resource.Success` возвращает `UiState.Success(mappedValue)`;
- при `Resource.Error` возвращает `UiState.Error(errorMapper(error), mappedCachedValue)`;
- по умолчанию маппит ошибку через `Throwable.toUiError()`.

Правила:

- Не создавай `UiState` вручную для результата usecase, если подходит `Resource.toUiState(...)`.
- Передавай в `mapper` domain-to-presentation mapper: `profileModelMapper::map`.
- Используй cached `model` из предыдущего `UiState`, когда переводишь экран в loading вручную: `UiState.Loading(state.profile.model)`.
- Если нужно преобразовать уже готовый `UiState<T>` в `UiState<R>`, используй `UiState.map { ... }` из `base/presentation`.
- Для side effects при смене состояния можно использовать `onLoading`, `onSuccess`, `onError` из `base/presentation`, но они не заменяют state modeling.
- Не отдавай `Resource<T>` в composable. UI должен работать с `UiState<T>`.

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
- Если фиче нужны специальные ошибки, добавь feature-specific `UiError` в presentation layer и передай custom `errorMapper` в `Resource.toUiState(...)`.
- Composable делает rendering по `UiError`, но не знает про `DataError.code`, exception classes или network implementation.

Пример feature-specific mapping:

```kotlin
internal class ProfileNotFoundUiError : UiError

private fun Throwable.toProfileUiError(): UiError {
    return when (this) {
        is DataError if code == PROFILE_NOT_FOUND_ERROR_CODE -> ProfileNotFoundUiError()
        else -> toUiError()
    }
}

val profileState = resource.toUiState(
    isLoading = false,
    errorMapper = { it.toProfileUiError() },
    mapper = profileModelMapper::map,
)
```

## Composable Rules

Composable слой делится на connector и pure screen:

- `*Content` принимает `component`, собирает `stateFlow` и передает state/events дальше.
- `*Screen` принимает `state`, `onEvent` и `modifier`, не знает про Decompose component.
- Мелкие composable принимают конкретные values и callbacks/events, а не весь component.

```kotlin
@Composable
internal fun ProfileContent(
    component: ProfileComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.stateFlow.collectAsStateWithLifecycle()

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
- Не собирай несколько flow из component в composable. Собирай единый `stateFlow`.
- Используй design system (`MyTheme`, базовые components, typography/colors) вместо локальных случайных цветов/типографики.
- Не держи business logic в composable. Сложные вычисления и форматирование выноси в mapper/component.
- Для platform behavior используй platform abstractions через component/usecase, а не прямые platform calls из UI.
- Preview/test composable должны работать через fake `State`, без настоящего component и DI.

## Anti-Patterns

- `fun onRetryClick()` / `fun onBackClick()` / `fun onQueryChanged(value: String)` в component interface вместо `onUIEvent(event)`.
- Несколько публичных `StateFlow` под разные куски экрана.
- `Resource<T>` или `Throwable` в `*State`.
- DTO/Entity в presentation model или composable.
- Mapper, который возвращает `UiState`.
- Composable, который принимает repository/usecase/component factory.
- Component с `@Composable fun View()`.
- Public feature api, который раскрывает presentation `State`, `Event`, component или `UiState`.
