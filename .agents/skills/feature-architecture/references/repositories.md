# Repositories

## Purpose

Repository в feature impl модуле является data-layer фасадом для domain слоя.

Repository:

- координирует `RemoteDataSource`, `LocalDataSource` и mapper'ы;
- владеет локальными cache/resource holder'ами, если repository нужен in-memory resource state;
- скрывает DTO, Entity, DataStore, database и network детали от domain/presentation;
- возвращает domain модели, `Resource<T>` или `Flow<Resource<T>>`;
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
- `ResourceHolder`/`LocalResourceHolder` не являются DI-зависимостью repository. Если holder нужен, repository создает его сам как private field.
- Repository может использовать domain модели как return/input модели.
- Repository не должен зависеть от presentation моделей, `UiState`, Compose, Decompose components, navigation callbacks или UI mapper'ов.
- Repository не должен принимать или возвращать DTO/Entity наружу, если метод используется domain слоем.
- Repository не должен импортировать feature API модели без необходимости. Публичные Args/Callbacks обычно остаются на navigation boundary, а не в data слое.

## Method Rules

- Метод repository должен описывать data operation на языке фичи: `getProfile`, `observeProfile`, `updateProfile`, `refreshProfile`.
- Для одноразового чтения используй `suspend fun`.
- Для наблюдения используй `fun observe*(): Flow<Resource<T>>` или `Flow<T>`, если errors невозможны или уже обработаны выше.
- Для команды записи используй `suspend fun` и возвращай `Resource<Unit>` или `Resource<DomainModel>`, если результат нужен вызывающему коду.
- При вызове любого метода `RemoteDataSource` из repository оборачивай remote-вызов в `fetch { ... }` из `base/network`.
- Не делай в одном методе несколько независимых сценариев. Если метод и читает профиль, и обновляет настройки, и отправляет аналитику, сценарий должен быть разделен.

## Network Fetch Rules

`fetch` из `com.tastyhome.base.network.fetch` применяется на repository уровне при вызове `RemoteDataSource`.

- `RemoteDataSource` отвечает только за один HTTP-запрос и возвращает сериализованный body/response.
- Repository вызывает remote метод через `fetch { remoteDataSource.someMethod(...) }`.
- `fetch` нормализует Ktor/network exceptions в project data errors.
- Если метод repository возвращает `Resource<T>`, оборачивай `fetch` в `runCatchingResource { ... }`.
- Не вызывай `RemoteDataSource` напрямую из repository без `fetch`.
- Не размещай `fetch` внутри `RemoteDataSource`, чтобы remote слой оставался тонкой оберткой над HTTP-запросом.

## Mapping Rules

- Remote DTO маппится в domain модель внутри repository через mapper из `data/mapper/`.
- Entity маппится в domain модель внутри repository через mapper из `data/mapper/`.
- Request body DTO создается в repository или data mapper из domain input/command args.
- Presentation модели создаются только в presentation mapper'ах, не в repository.

## Resource Rules

`Resource<T>` используется как domain/data результат операции:

- `Resource.Success(value)` означает успешное получение или изменение данных.
- `Resource.Error(error, value = cachedValue)` означает ошибку, при которой можно опционально сохранить последнее известное значение.
- `Resource.Error(error)` без value используется, когда fallback данных нет.
- `runCatchingResource { ... }` используй для простого оборачивания операции, которая может бросить исключение и возвращает nullable result.
- `CancellationException` не нужно проглатывать: `runCatchingResource` уже пробрасывает cancellation дальше.
- `Resource.map { ... }` используй для преобразования значения без потери error состояния.
- `Flow<Resource<T>>.mapResource { ... }` используй для преобразования flow с сохранением `Success/Error`.

Repository должен возвращать `Resource<DomainModel>`, а не `Result`, nullable domain model или DTO, если в фиче нужно передавать ошибку наверх.

## ResourceHolder Rules

`ResourceHolder<T>` и `LocalResourceHolder<T>` используются, когда repository должен хранить последнее состояние ресурса и отдавать его как `Flow<Resource<T>>`.

Holder'ы являются локальной деталью repository. Они не создаются в `ProfileGraph`, не описываются в `InternalBindings`, не передаются через `ParentDependencies` и не инжектятся в constructor repository.

Даже если сам repository создается через Metro, holder остается обычным private field внутри repository.

Используй `LocalResourceHolder<T>`, когда:

- фиче нужен in-memory cache для одного ресурса;
- нет отдельного persistent `CacheHolder`;
- данные живут только в рамках конкретного instance repository.

Используй `ResourceHolder<T>`, когда:

- есть отдельный `CacheHolder<T?>`, связанный с локальным источником данных;
- нужно совместить локальные cached data и последнее error состояние;
- repository наблюдает cache как `Flow<Resource<T>>`.

Если для `ResourceHolder` нужен `CacheHolder`, собирай `CacheHolder` локально в repository из уже существующего local source. Не выноси `CacheHolder`/`ResourceHolder` в DI graph.

Update strategy:

- `DefaultResourceUpdateStrategy.Straight` сохраняет новый `Resource` как есть.
- `DefaultResourceUpdateStrategy.DataStoresOnError` используется как стратегия по умолчанию для `ResourceHolder` и предназначена для сценариев, где error state может сопровождаться сохраненными данными.

Правила применения holder'ов:

- Holder создается внутри repository как `private val`.
- Holder не передается в constructor repository.
- Holder не инжектится через Metro.
- Holder не создается в `InternalBindings`, `AppBindings` или любом другом DI binding container.
- Holder не передается через `ParentDependencies`.
- Наружу отдавай `holder.observe()`, а не сам holder.
- Обновляй holder после remote/local операции через `holder.update(resource)`.
- Не обновляй holder из presentation слоя.
- Не используй holder вместо persistent storage, если данные должны переживать перезапуск приложения.
- Не используй holder для независимых ресурсов вперемешку. Один holder хранит один тип ресурса.

Неправильно:

```kotlin
internal class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val mapper: ProfileMapper,
    private val profileHolder: LocalResourceHolder<Profile>,
)
```

```kotlin
@BindingContainer
internal abstract class ProfileInternalBindings {
    companion object {
        @Provides
        internal fun profileHolder(): LocalResourceHolder<Profile> {
            return LocalResourceHolder()
        }
    }
}
```

Эти примеры неправильные, потому что holder становится частью DI graph. Holder должен оставаться private implementation detail конкретного repository.

## Error Handling Rules

- Repository ловит technical/data ошибки и превращает их в `Resource.Error`.
- Repository не превращает ошибки в UI text. UI error mapping выполняется в presentation слое.
- Repository не должен скрывать ошибку пустой domain моделью, если это не явное бизнес-правило.
- Если при ошибке есть cached value, возвращай `Resource.Error(error, cachedValue)`.
- Если данных нет, возвращай `Resource.Error(error)` или `Resource.Error(emptyDataError())`, когда ошибка именно в отсутствии данных.

## Correct Example: Remote Read

```kotlin
internal class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val mapper: ProfileMapper,
) {
    suspend fun getProfile(profileId: ProfileId): Resource<Profile> {
        return runCatchingResource {
            val dto = fetch { remoteDataSource.getProfile(profileId.value) }
            mapper.toDomain(dto)
        }
    }
}
```

## Correct Example: Observe Cached Resource

```kotlin
internal class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val mapper: ProfileMapper,
) {
    // Local repository state, not a DI dependency.
    private val profileHolder = LocalResourceHolder<Profile>()

    fun observeProfile(): Flow<Resource<Profile>> {
        return profileHolder.observe()
    }

    suspend fun refreshProfile(profileId: ProfileId): Resource<Profile> {
        val resource = runCatchingResource {
            val dto = fetch { remoteDataSource.getProfile(profileId.value) }
            mapper.toDomain(dto)
        }
        profileHolder.update(resource)
        return resource
    }
}
```

## Correct Example: Mapping Resource Flow

```kotlin
internal class ProfileRepository(
    private val localDataSource: ProfileLocalDataSource,
    private val mapper: ProfileMapper,
) {
    fun observeProfile(): Flow<Resource<Profile>> {
        return localDataSource.observeProfileEntity()
            .mapResource { entity -> mapper.toDomain(entity) }
    }
}
```

## Incorrect Example

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
