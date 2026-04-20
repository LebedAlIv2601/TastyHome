# Metro DI

## Purpose

DI в фичах служит для сборки internal зависимостей feature impl модуля и экспорта наружу только публичной factory из api модуля.

Feature impl модуль подключается только в `shared`. Для root app graph фича становится видимой через `FeatureBindings`, куда добавляется `*AppBindings` фичи.

## File Location

DI фичи лежит в папке `di/` внутри impl модуля:

```text
features/
└── profile/
    └── impl/
        └── src/
            └── commonMain/
                └── kotlin/
                    └── com/
                        └── tastyhome/
                            └── profile/
                                └── di/
                                    └── ProfileGraph.kt
```

Для single-screen фичи обычно достаточно одного файла `*Graph.kt` в `commonMain`.

Для flow-фичи графы разделяются по scopes:

```text
di/
├── ProfileRootGraph.kt
├── ProfileDetailsGraph.kt
└── ProfileEditGraph.kt
```

Root graph создает root component и root-scope зависимости flow. Каждый внутренний экран имеет свой screen graph и свой screen scope.

Platform-specific bindings добавляются в `androidMain`/`iosMain` только если зависимость нельзя описать в `commonMain`.

## Naming

Все DI declarations должны называться с приставкой названия фичи или конкретного экрана flow:

- `ProfileScope`
- `ProfileParentDependencies`
- `ProfileGraph`
- `ProfileInternalBindings`
- `ProfileAppBindings`
- `ProfileRootScope`
- `ProfileRootGraph`
- `ProfileDetailsScope`
- `ProfileDetailsGraph`
- `ProfileDetailsAppBindings`

Не используй безымянные `Graph`, `Scope`, `InternalBindings`, `AppBindings` как top-level имена. В feature impl модуле со временем может появиться несколько графов или binding containers, поэтому имя должно сразу показывать владельца.

## Required Structure

Файл `ProfileGraph.kt` содержит top-level declarations. Не оборачивай их в `class ProfileGraph`.

Правильная форма для single-screen фичи:

```kotlin
@Scope
annotation class ProfileScope

@Inject
data class ProfileParentDependencies(
    val appDatabase: AppDatabase,
    val profileDataStore: ProfileDataStore,
)

@DependencyGraph(
    ProfileScope::class,
    bindingContainers = [
        ProfileInternalBindings::class,
    ],
)
internal interface ProfileGraph {

    fun profileComponentFactory(): ProfileComponent.Factory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Includes
            parentDependencies: ProfileParentDependencies,
        ): ProfileGraph
    }
}

@BindingContainer
internal abstract class ProfileInternalBindings {

    @Binds
    internal abstract val DefaultProfileComponent.Factory.bind: ProfileComponent.Factory
}

@BindingContainer
abstract class ProfileAppBindings {

    @Binds
    internal abstract val ProfileFeatureFactoryImpl.bind: ProfileFeatureFactory

    companion object {
        @SingleIn(AppScope::class)
        @Provides
        internal fun provideProfileDataStore(
            factory: DataStoreFactory,
        ): ProfileDataStore {
            return ProfileDataStore(factory.create(filename = "profile_store"))
        }
    }
}
```

Это пример формы, а не обязательный набор зависимостей. Если фиче не нужен `DataStore`, `AppDatabase` или отдельная component factory, соответствующие поля и bindings не добавляются.

## Flow Graph Structure

Для flow-фичи public factory создает root graph. Root graph подключает AppBindings внутренних экранов, но только внутри impl модуля фичи.

```kotlin
@Scope
annotation class ProfileRootScope

@DependencyGraph(
    ProfileRootScope::class,
    bindingContainers = [
        ProfileRootInternalBindings::class,
        ProfileDetailsAppBindings::class,
        ProfileEditAppBindings::class,
    ],
)
internal interface ProfileRootGraph {

    fun profileRootComponentFactory(): ProfileRootComponent.Factory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Includes parentDependencies: ProfileRootParentDependencies,
        ): ProfileRootGraph
    }
}
```

Внутренний экран имеет свой graph и scope:

```kotlin
@Scope
annotation class ProfileDetailsScope

@Inject
data class ProfileDetailsParentDependencies(
    val profileRepository: ProfileRepository,
)

@DependencyGraph(
    ProfileDetailsScope::class,
    bindingContainers = [
        ProfileDetailsInternalBindings::class,
    ],
)
internal interface ProfileDetailsGraph {

    fun profileDetailsComponentFactory(): ProfileDetailsComponent.Factory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Includes parentDependencies: ProfileDetailsParentDependencies,
        ): ProfileDetailsGraph
    }
}
```

Правила:

- `ProfileAppBindings` публичной фичи подключается в `shared`.
- `ProfileDetailsAppBindings` и другие AppBindings внутренних экранов подключаются в `ProfileRootGraph`, а не в `shared`.
- Internal screen `*FeatureFactory` получает `*ParentDependencies`, создает свой screen graph через `createGraphFactory` и возвращает `Feature`.
- Root component получает internal screen `*FeatureFactory` через DI и в `createChild` только вызывает их `create(...)`.
- Root component не создает screen components напрямую и не знает screen graph.

## Scope Rules

Feature/screen scope создается top-level annotation class:

```kotlin
@Scope
annotation class ProfileScope
```

Feature scope используется только внутри своего graph.

`AppScope` используется только для объектов, которые должны жить на уровне всего приложения:

- feature-specific DataStore;
- database;
- app-wide managers/services;
- другие тяжелые или stateful объекты, которые должны быть singleton на все приложение.

Feature/screen scope используется для объектов, которые должны быть singleton только внутри одного graph:

- repository с in-memory state;
- coordinator/state holder внутри flow;
- дорогой объект, который должен переиспользоваться внутри фичи, но не на уровне всего приложения.

Не ставь `@SingleIn` на DTO/domain/presentation модели, stateless mapper'ы, простые stateless use case, Decompose components конкретного экрана и factories, которые должны создавать новые instances.

## ParentDependencies

`ProfileParentDependencies` является только собирательным классом для внешних зависимостей, которые parent graph передает в текущий graph.

В `ProfileParentDependencies` помещаются:

- зависимости из `base/*` и `core/*`;
- feature-specific DataStore, созданный в `ProfileAppBindings`;
- `AppDatabase`, если фиче нужна база;
- managers из `base/platform`;
- api интерфейсы core модулей;
- api интерфейсы других features, если такая зависимость разрешена архитектурно.

В `ProfileParentDependencies` не помещаются internal классы текущей фичи:

- `RemoteDataSource`;
- `LocalDataSource`;
- repository;
- use case;
- mapper;
- component;
- presentation model.

`ProfileParentDependencies` передается в public или internal `*FeatureFactory`, а внутри factory implementation используется для создания соответствующего graph.

`ProfileParentDependencies` передается в factory method создания graph с аннотацией `@Includes`:

```kotlin
@DependencyGraph.Factory
fun interface Factory {
    fun create(
        @Includes
        parentDependencies: ProfileParentDependencies,
    ): ProfileGraph
}
```

Metro подтягивает поля `ProfileParentDependencies` в graph через `@Includes`. Не нужно писать provider-функции вида:

```kotlin
@Provides
internal fun appDatabase(
    parentDependencies: ProfileParentDependencies,
): AppDatabase {
    return parentDependencies.appDatabase
}
```

Такие pass-through providers запрещены: зависимости из `ParentDependencies` должны попадать в graph через `@Includes` на параметре factory method.

## InternalBindings

`ProfileInternalBindings` содержит только bindings, которые нужны внутри своего `*Graph`.

Сюда помещаются:

- bindings внутренних интерфейсов фичи к реализациям;
- bindings component factories;
- bindings internal screen dependencies, если они нужны только внутри screen graph;
- providers для объектов, которые нужны только внутри feature graph.

Сюда не помещаются:

- публичная feature factory из api модуля;
- DataStore или другие app-level singletons;
- bindings, которые должен видеть parent/root graph;
- bindings других features.

`ProfileInternalBindings` может быть `internal`, потому что он используется только внутри impl модуля фичи.

Пример:

```kotlin
@BindingContainer
internal abstract class ProfileInternalBindings {

    @Binds
    internal abstract val DefaultProfileComponent.Factory.bind: ProfileComponent.Factory
}
```

## AppBindings

`ProfileAppBindings` подключается в основной DI graph в `shared`, поэтому сам binding container не должен быть `internal`.

Сюда помещаются:

- binding `ProfileFeatureFactoryImpl` к `ProfileFeatureFactory` из api модуля;
- feature-specific DataStore;
- другие объекты, которые должны быть singleton на все приложение.

Сюда не помещаются:

- internal dependencies фичи;
- component instances конкретного экрана;
- feature-flow state;
- bindings других features.

Пример:

```kotlin
@BindingContainer
abstract class ProfileAppBindings {

    @Binds
    internal abstract val ProfileFeatureFactoryImpl.bind: ProfileFeatureFactory

    companion object {
        @SingleIn(AppScope::class)
        @Provides
        internal fun provideProfileDataStore(
            factory: DataStoreFactory,
        ): ProfileDataStore {
            return ProfileDataStore(factory.create(filename = "profile_store"))
        }
    }
}
```

Если у фичи нет app-level singletons, в `ProfileAppBindings` остается только binding feature factory.

Internal screen `*AppBindings` подключаются не в `shared`, а в root graph фичи. Они могут быть `internal`.

```kotlin
@BindingContainer
internal abstract class ProfileDetailsAppBindings {

    // Здесь bind/provide только то, что root graph должен использовать
    // для создания ProfileDetailsFeatureFactory или ProfileDetailsParentDependencies.
}
```

## Shared Wiring

В `shared/src/commonMain/.../FeatureBindings.kt` добавляется `ProfileAppBindings`:

```kotlin
@BindingContainer(
    includes = [
        ProfileAppBindings::class,
    ],
)
internal interface FeatureBindings
```

Если фич несколько, все их `*AppBindings` добавляются в общий список `FeatureBindings.includes`.

Feature impl module подключается только в `shared`. Другие feature/core/base модули не должны зависеть от impl модуля фичи.

## FeatureFactoryImpl Flow

Фичи оперируют factory из api модуля. Implementation этой factory создает root graph для flow или screen graph для single-screen фичи.

Graph factory не провайдится через DI. Она создается внутри `ProfileFeatureFactoryImpl` через `createGraphFactory<ProfileGraph.Factory>()` или `createGraphFactory<ProfileRootGraph.Factory>()`.

Пример:

```kotlin
@Inject
internal class ProfileFeatureFactoryImpl(
    private val parentDependencies: ProfileParentDependencies,
) : ProfileFeatureFactory {

    override fun create(
        componentContext: ComponentContext,
        args: ProfileArgs,
        callbacks: ProfileCallbacks,
    ): Feature {
        val graph = createGraphFactory<ProfileGraph.Factory>()
            .create(parentDependencies)

        val component = graph.profileComponentFactory()
            .create(
                componentContext = componentContext,
                args = args,
                callbacks = callbacks,
            )

        return Feature { modifier ->
            ProfileContent(
                component = component,
                modifier = modifier,
            )
        }
    }
}
```

Имена методов должны соответствовать реальному contract фичи. Важный принцип: `ProfileFeatureFactoryImpl` получает `ProfileParentDependencies`, создает graph, создает Decompose component и наружу отдает только `Feature`.

Для внутреннего экрана flow используется такой же принцип, но factory остается internal:

```kotlin
@Inject
internal class ProfileDetailsFeatureFactory(
    private val parentDependencies: ProfileDetailsParentDependencies,
) {
    fun create(
        componentContext: ComponentContext,
        profileId: String,
        router: ProfileRouter,
    ): Feature {
        val graph = createGraphFactory<ProfileDetailsGraph.Factory>()
            .create(parentDependencies)

        val component = graph.profileDetailsComponentFactory()
            .create(
                componentContext = componentContext,
                profileId = profileId,
                router = router,
            )

        return Feature { modifier ->
            ProfileDetailsScreen(
                component = component,
                modifier = modifier,
            )
        }
    }
}
```

## DataStore Rules

Feature-specific DataStore создается в `ProfileAppBindings`, если он должен быть singleton на все приложение.

Правильно:

```kotlin
@BindingContainer
abstract class ProfileAppBindings {
    companion object {
        @SingleIn(AppScope::class)
        @Provides
        internal fun provideProfileDataStore(
            factory: DataStoreFactory,
        ): ProfileDataStore {
            return ProfileDataStore(factory.create(filename = "profile_store"))
        }
    }
}
```

Неправильно:

```kotlin
@BindingContainer
internal object ProfileInternalBindings {
    @Provides
    fun provideProfileDataStore(factory: DataStoreFactory): ProfileDataStore {
        return ProfileDataStore(factory.create(filename = "profile_store"))
    }
}
```

Этот пример неправильный, потому что DataStore создается внутри feature graph, а не на уровне app graph.

## Checklist

- Для single-screen фичи файл называется `ProfileGraph.kt` и лежит в `di/`.
- Для flow-фичи root и screen graphs лежат в отдельных файлах внутри `di/`.
- DI declarations не обернуты в отдельный class.
- Scope объявлен как `@Scope annotation class ProfileScope`.
- Graph называется с приставкой фичи: `ProfileGraph`.
- `ProfileGraph.Factory.create(...)` принимает `@Includes parentDependencies: ProfileParentDependencies`.
- Для полей `ParentDependencies` не написаны pass-through provider-функции.
- `ProfileParentDependencies` передается в `ProfileFeatureFactoryImpl`.
- `ProfileFeatureFactoryImpl` создает graph через `createGraphFactory<ProfileGraph.Factory>()`.
- Internal screen `*FeatureFactory` создает свой screen graph через `createGraphFactory<ProfileDetailsGraph.Factory>()`.
- `ProfileInternalBindings` содержит только internal bindings фичи.
- `ProfileAppBindings` не `internal` и подключается в `shared` через `FeatureBindings.includes`.
- Internal screen `*AppBindings` не подключаются в `shared`; они подключаются в root graph фичи.
- Feature factory binding находится в `ProfileAppBindings`.
- DataStore и другие app-wide singletons создаются в `ProfileAppBindings`.
- ResourceHolder и LocalResourceHolder не создаются в DI graph, а локально внутри repository.
- Components и screen state не являются app singletons.
