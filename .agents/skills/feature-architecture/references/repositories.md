# Repositories

## Purpose

Repository в feature impl модуле является data-layer фасадом для domain слоя.

Repository:

- координирует `RemoteDataSource`, `LocalDataSource` и mapper'ы;
- скрывает DTO, Entity, DataStore, database и network детали от domain/presentation;
- для данных экрана отдает observable local source (`Flow<Domain?>`, `Flow<List<Domain>?>`, `Flow<Domain>`);
- хранит data/query state, который влияет на состав, параметры, сортировку, фильтрацию или другие показатели основных данных;
- для remote sync/command операций возвращает `ResultStatus`;
- для одноразовых операций с payload возвращает `Result<T>`;
- содержит orchestration data-операций, но не содержит UI-логику.

Repository не должен быть просто папкой для любого кода. Если логика относится к одному источнику данных, она должна остаться в `RemoteDataSource` или `LocalDataSource`; если это бизнес-сценарий, она должна быть в use case.

## Location

- Repository файлы лежат в `data/` на одном уровне с `remote/`, `local/`, `model/`, `mapper/`.
- Имя заканчивается на `Repository`: `ProfileRepository`, `RecipeRepository`.
- Один repository обслуживает одну связанную область данных фичи.
- Если фича не работает с данными, repository не создается.

```text
data/
├── ProfileRepository.kt
├── local/
│   └── ProfileLocalDataSource.kt
├── mapper/
│   └── ProfileMapper.kt
├── model/
│   └── ProfileDTO.kt
└── remote/
    └── ProfileRemoteDataSource.kt
```

## Dependency Rules

- Repository может зависеть от `RemoteDataSource`, `LocalDataSource`, mapper'ов и других data-layer helpers своей фичи.
- Repository может использовать domain модели как return/input модели.
- Repository не должен зависеть от presentation моделей, `UiState`, Compose, Decompose components, navigation callbacks или UI mapper'ов.
- Repository не должен принимать или возвращать DTO/Entity наружу, если метод используется domain слоем.
- Repository не должен импортировать feature API модели без необходимости. Публичные Args/Callbacks обычно остаются на navigation boundary, а не в data слое.

## State Ownership Rules

Repository владеет состоянием, которое является частью data/query модели фичи:

- примененные фильтры, сортировки, поисковые query и paging/query params;
- выбранные data-срезы, которые меняют состав observable данных;
- параметры remote/local data request, cache key или local projection;
- состояние, которое должно переживать пересоздание component в рамках feature scope;
- состояние, которое при отдельном требовании к персистентности должно быть перенесено из `MutableStateFlow` в `DataStore`, Room или другой local source.

Такое состояние оформляй доменной моделью и обновляй целиком через repository:

```kotlin
internal data class RecipeFilters(
    val maxCookingTimeMinutes: Int? = null,
    val sort: RecipeSort = RecipeSort.Default,
)
```

```kotlin
private val filtersFlow = MutableStateFlow(RecipeFilters())

fun observeFilters(): Flow<RecipeFilters> = filtersFlow

fun updateFilters(filters: RecipeFilters) {
    filtersFlow.value = filters
}
```

Не дроби update-методы на `updateMaxCookingTime`, `updateSort`, `updateCategory`, если фильтры являются единой доменной query-моделью. Дроби методы только когда это разные бизнес-команды с разными правилами/побочными эффектами.

Repository не должен хранить UI/process state:

- transient loading flag;
- цвет, enabled/disabled state, expanded/collapsed state, focus, hover;
- dialog visibility, snackbar text, sheet state;
- draft-значение UI-контрола, которое еще не применено к data query;
- navigation state и callbacks.

Такое состояние остается в component.

## Screen Data Rules

Если данные отображаются на экране, remote response не является источником UI-data напрямую.

Правильный поток:

```text
remote request -> validate/map -> save to local source -> return ResultStatus
local observable source -> domain Flow -> component createUiState(...)
```

Правила:

- Экранные данные наружу отдаются через `observe*(): Flow<Domain?>`, `Flow<List<Domain>?>` или другой observable domain flow из локального источника.
- Data/query state, который меняет наполнение экранных данных, наружу отдается через `observe*Filters()`, `observe*Query()` или другой observable domain flow из repository.
- Изменение data/query state выполняется через `update*Filters(filters)`, `update*Query(query)` или другой repository метод, принимающий доменную модель целиком.
- Remote sync метод (`refresh*`, `sync*`, `load*`) возвращает только `ResultStatus`.
- Remote sync метод должен записывать успешный результат в локальный source (`Room`, `DataStore`, in-memory `MutableStateFlow` и т.д.).
- Component собирает `UiState` из observable data, UI loading flag и `ResultStatus` через helper из `base/presentation`.
- Не возвращай экранные данные из remote sync метода, если эти же данные должны отображаться через локальный source.
- Не держи параллельную правду: UI не должен одновременно брать свежий network payload и cached data из локального источника.

## Command And One-Shot Rules

Для операций без отображаемого payload:

- Возвращай `ResultStatus`, если UI нужен только факт успеха/ошибки.
- Если command меняет данные, которые отображаются на экране, после успешной операции обнови локальный source или инвалидируй/перезагрузи его.
- Если command не влияет на данные экрана, он не обязан ничего сохранять локально.

Для одноразовых операций с payload:

- Возвращай `Result<T>`, если payload нужен component прямо сейчас и не является screen source of truth.
- Ошибки оставляй в `Result`, а UI mapping делай в presentation через `Throwable.toUiError()`.

## Method Rules

- Метод repository должен описывать data operation на языке фичи: `observeProfile`, `refreshProfile`, `updateProfile`, `deleteProfile`.
- Для наблюдения используй `fun observe*(): Flow<T?>` или `Flow<List<T>?>`.
- Для data/query state используй пару `fun observe*Filters(): Flow<DomainFilters>` и `fun update*Filters(filters: DomainFilters)`, либо аналогичные `Query`-методы.
- Для remote sync экранных данных используй `suspend fun refresh*(): ResultStatus`.
- Для command без payload используй `suspend fun update*(): ResultStatus`, `delete*(): ResultStatus`, `create*(): ResultStatus`.
- Для one-shot payload используй `suspend fun get*(): Result<T>`, только если этот payload не является screen source of truth.
- При вызове любого метода `RemoteDataSource` из repository оборачивай remote-вызов в `fetch { ... }` из `base/network`.
- Не делай в одном методе несколько независимых сценариев. Если метод и читает профиль, и обновляет настройки, и отправляет аналитику, сценарий должен быть разделен.

## Network Fetch And Result Rules

`fetch` из `com.tastyhome.base.network.fetch` применяется на repository уровне при вызове `RemoteDataSource`.

- `RemoteDataSource` отвечает только за один HTTP-запрос и возвращает сериализованный body/response.
- Repository вызывает remote метод через `fetch { remoteDataSource.someMethod(...) }`.
- `fetch` нормализует Ktor/network exceptions в project data errors.
- Remote sync методы оборачивай в `runForResult { ... }.status()`.
- One-shot payload методы оборачивай в `runForResult { ... }`.
- `runForResult` должен охватывать и network/mapping, и локальное сохранение, чтобы ошибка записи в БД/хранилище стала ошибкой операции.
- Не вызывай `RemoteDataSource` напрямую из repository без `fetch`.
- Не размещай `fetch` внутри `RemoteDataSource`, чтобы remote слой оставался тонкой оберткой над HTTP-запросом.

### runForResult Return Rule

Для remote sync списка `runForResult` должен возвращать именно список, полученный из сети и прошедший domain/data mapping.

Локальное сохранение является side effect внутри block:

- если сохранение успешно, результат операции определяется возвращенным списком;
- если сохранение падает, exception влияет на результат и превращает операцию в ошибку;
- block не должен возвращать `Unit` от save-операции, иначе empty-list validation не сможет проверить payload.

Правильно:

```kotlin
suspend fun refreshRecipes(): ResultStatus {
    return runForResult {
        val response = fetch { remoteDataSource.getRecipes() }
        val recipes = recipeMapper.toDomain(response)
        localDataSource.replaceRecipes(recipeMapper.toEntities(recipes))
        recipes
    }.status()
}
```

Неправильно:

```kotlin
suspend fun refreshRecipes(): ResultStatus {
    return runForResult {
        val response = fetch { remoteDataSource.getRecipes() }
        localDataSource.replaceRecipes(
            recipeMapper.toEntities(recipeMapper.toDomain(response))
        )
    }.status()
}
```

Этот пример неправильный, потому что block возвращает результат сохранения, а не список из сети. Empty payload может стать успешной операцией, если save вернул `Unit`.

## Mapping Rules

- Remote DTO маппится в domain модель внутри repository через mapper из `data/mapper/`.
- Entity маппится в domain модель внутри repository через mapper из `data/mapper/`.
- Request body DTO создается в repository или data mapper из domain input/command args.
- Presentation модели создаются только в presentation mapper'ах, не в repository.

## Error Handling Rules

- Repository ловит technical/data ошибки через `runForResult`.
- Repository не превращает ошибки в UI text. UI error mapping выполняется в presentation слое.
- Repository не должен скрывать ошибку пустой domain моделью, если это не явное бизнес-правило.
- Если данных нет, используй project data error вроде `emptyDataError()`, когда ошибка именно в отсутствии данных.
- `CancellationException` не нужно проглатывать: `runForResult` пробрасывает cancellation дальше.

## Correct Example: Observable Screen Data

```kotlin
internal data class RecipeFilters(
    val maxCookingTimeMinutes: Int? = null,
    val sort: RecipeSort = RecipeSort.Default,
)

internal class RecipesRepository(
    private val localDataSource: RecipeLocalDataSource,
    private val remoteDataSource: RecipeRemoteDataSource,
    private val recipeMapper: RecipeMapper,
) {
    private val filtersFlow = MutableStateFlow(RecipeFilters())

    fun observeRecipes(): Flow<List<Recipe>?> {
        return localDataSource.observeRecipes()
            .map { entities -> entities?.let(recipeMapper::toDomain) }
    }

    fun observeFilters(): Flow<RecipeFilters> {
        return filtersFlow
    }

    fun updateFilters(filters: RecipeFilters) {
        filtersFlow.value = filters
    }

    suspend fun refreshRecipes(): ResultStatus {
        return runForResult {
            val response = fetch { remoteDataSource.getRecipes() }
            val recipes = recipeMapper.toDomain(response)
            localDataSource.replaceRecipes(recipeMapper.toEntities(recipes))
            recipes
        }.status()
    }
}
```

Этот пример правильный, потому что экранные данные читаются только из локального observable source, примененные фильтры хранятся как data/query state repository, а remote request возвращает только статус синхронизации.

## Correct Example: Command Without Payload

```kotlin
internal class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: ProfileLocalDataSource,
) {
    suspend fun deleteProfile(profileId: ProfileId): ResultStatus {
        return runForResult {
            fetch { remoteDataSource.deleteProfile(profileId.value) }
            localDataSource.deleteProfile(profileId.value)
            true
        }.status()
    }
}
```

Этот пример правильный, потому что операция не возвращает отображаемые данные, но после успешного remote command обновляет локальный source, который питает экран.

## Correct Example: One-Shot Payload

```kotlin
internal class InviteRepository(
    private val remoteDataSource: InviteRemoteDataSource,
    private val mapper: InviteMapper,
) {
    suspend fun createInviteLink(profileId: ProfileId): Result<InviteLink> {
        return runForResult {
            val dto = fetch { remoteDataSource.createInviteLink(profileId.value) }
            mapper.toDomain(dto)
        }
    }
}
```

Этот пример правильный, если invite link нужен как одноразовый payload для dialog/share flow и не является screen source of truth.

## Incorrect Example: Returning Network Data For Screen State

```kotlin
internal class RecipesRepository(
    private val remoteDataSource: RecipeRemoteDataSource,
    private val recipeMapper: RecipeMapper,
) {
    suspend fun refreshRecipes(): Result<List<Recipe>> {
        return runForResult {
            val response = fetch { remoteDataSource.getRecipes() }
            recipeMapper.toDomain(response)
        }
    }
}
```

Этот пример неправильный для экранных данных, потому что UI начнет зависеть от network payload вместо единого observable local source.

## Incorrect Example: Presentation Leakage

```kotlin
internal class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val modelMapper: ProfileModelMapper,
) {
    suspend fun getProfile(onOpenProfile: () -> Unit): UiState<ProfileModel> {
        val dto = remoteDataSource.getProfile()
        val model = modelMapper.toModel(dto)
        onOpenProfile()
        return UiState.Success(model)
    }
}
```

Этот пример неправильный, потому что repository зависит от presentation mapper'а, возвращает `UiState`, принимает UI callback, маппит DTO напрямую в presentation model и вызывает `RemoteDataSource` без `fetch`.

## Incorrect Example: UI State In Repository

```kotlin
internal class RecipesRepository {
    private val loadingFlow = MutableStateFlow(false)
    private val isFilterSheetOpenFlow = MutableStateFlow(false)
    private val sliderDraftValueFlow = MutableStateFlow(30)
}
```

Этот пример неправильный, потому что loading, sheet visibility и draft-значение контрола являются UI/process state component, а не data/query state repository.
