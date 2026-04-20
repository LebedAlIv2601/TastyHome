# Decompose Navigation

## Purpose

Этот файл описывает правила feature navigation в TastyHome: публичный contract в api модуле, реализацию factory в impl модуле, root/screen DI graphs, внутреннюю навигацию через Decompose и применение анимаций/shared transition.

Опорные классы проекта:

- `Feature`, `FeatureFactory`, `DeeplinkableFeatureFactory`, `Args`, `Callbacks` из `base/navigation/api`.
- `Router`, `BaseComponent`, `BaseParentComponent` из `base/navigation`.
- `LocalAnimatedVisibilityScope`, `LocalSharedTransitionScope` из `base/navigation/animations`.

В текущей кодовой базе отдельного `ParentComponent` типа нет. Для parent/root компонентов фич используется `BaseParentComponent`; если позже появится отдельный public/internal `ParentComponent` interface, он должен быть тонким контрактом поверх `BaseParentComponent`, а не заменой его stack-логики.

## API Module Contract

В api модуле ui-фичи объявляется ровно одна публичная factory, представляющая вход в фичу.

Для single-screen фичи factory открывает этот экран. Для flow-фичи factory открывает root flow component, который внутри себя переключает child features.

```kotlin
interface ProfileFeatureFactory : FeatureFactory<ProfileArgs, ProfileCallbacks>

data class ProfileArgs(
    val profileId: String,
) : Args

fun interface ProfileCallbacks : Callbacks {
    fun goBack()
}
```

Правила:

- Используй `EmptyArgs`, `NoCallbacks` или `BackOnlyCallback`, если фиче не нужны собственные args/callbacks.
- Если фича поддерживает deeplink как публичный вход, factory должна наследоваться от `DeeplinkableFeatureFactory<ARGS, CALLBACKS>` и реализовать `canHandle`/`parseArgs` в impl.
- В api модуле не должно быть Decompose components, `Config`, child factories, graph, DI, screen state, composable или реализации factory.
- В api модуле flow-фичи не объявляй factory для каждого внутреннего экрана. Внутренние экраны получают internal `*FeatureFactory` в impl модуле.
- Args и Callbacks являются публичным contract. Не клади туда data/presentation модели impl модуля.

## Public FeatureFactory

Implementation публичной factory лежит в `presentation/<root-or-screen>/navigation`.

Для flow-фичи public `*FeatureFactoryImpl` создает root graph, root Decompose component и возвращает `Feature { }`.

```kotlin
@Inject
internal class ProfileFeatureFactoryImpl(
    private val parentDependencies: ProfileRootParentDependencies,
) : ProfileFeatureFactory {

    override fun create(
        componentContext: ComponentContext,
        args: ProfileArgs,
        callbacks: ProfileCallbacks,
    ): Feature {
        val graph = createGraphFactory<ProfileRootGraph.Factory>()
            .create(parentDependencies)

        val component = graph.profileRootComponentFactory()
            .create(
                componentContext = componentContext,
                args = args,
                callbacks = callbacks,
            )

        return Feature { modifier ->
            ProfileRootContent(
                component = component,
                modifier = modifier,
            )
        }
    }
}
```

Правила:

- Graph factory не инжектится через Metro. Создавай ее через `createGraphFactory<ProfileRootGraph.Factory>()`.
- Root graph создается на каждый вызов `create(...)`, чтобы feature flow получил собственный root scope.
- Наружу возвращается только `Feature`/`DeeplinkableFeature`; internal graph, Decompose components, repositories, use cases и component factories не протекают в api.
- `componentContext` всегда приходит снаружи через `FeatureFactory.create(...)` и передается в root component/screen factory.
- Для flow-фичи public factory создает только root component. Child screens создаются через internal screen `*FeatureFactory`.
- Если публичная factory deeplinkable, возвращай `DeeplinkableFeature(component::onDeeplink) { ... }`, а не заставляй component наследоваться от `Feature`.

## Flow DI Graphs

У flow-фичи есть root scope и отдельные scopes подэкранов.

Все graph файлы лежат в папке `di/`:

```text
di/
├── ProfileRootGraph.kt
├── ProfileDetailsGraph.kt
└── ProfileEditGraph.kt
```

Root graph экспортирует factory root component и подключает app bindings внутренних экранов. Screen graph экспортирует factory своего screen component.

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

```kotlin
@Scope
annotation class ProfileDetailsScope

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

- Public `ProfileAppBindings` подключается только в `shared` и bind-ит public `ProfileFeatureFactoryImpl` к api interface.
- Internal screen `*AppBindings` не подключаются в `shared`; они подключаются в root graph фичи.
- Root graph содержит root-scope зависимости flow и internal screen factories, нужные root component.
- Screen graph содержит зависимости своего подэкрана и имеет отдельный screen scope.
- Screen `*ParentDependencies` собирается в root graph из root-flow зависимостей и зависимостей, пришедших из app graph.
- Internal screen `*FeatureFactory` создает screen graph через `createGraphFactory<ProfileDetailsGraph.Factory>()`.
- Component factories не должны быть `@SingleIn(AppScope::class)`: каждый screen/component создается под конкретный `ComponentContext`.

## Components

Обычный screen component наследуется от `BaseComponent<RouterType>`, но не реализует `Feature`. `Feature` создается в navigation layer через функцию `Feature { }` и внутри вызывает composable, принимающую component.

```kotlin
internal interface ProfileDetailsComponent {

    val stateFlow: StateFlow<ProfileDetailsState>

    fun onUIEvent(event: ProfileDetailsEvent)

    @AssistedFactory
    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
            profileId: String,
            router: ProfileRouter,
        ): ProfileDetailsComponent
    }
}

@AssistedInject
internal class DefaultProfileDetailsComponent(
    @Assisted componentContext: ComponentContext,
    @Assisted private val profileId: String,
    @Assisted private val router: ProfileRouter,
) : BaseComponent<ProfileRouter>(
    router = router,
    componentContext = componentContext,
), ProfileDetailsComponent
```

Правила:

- Runtime параметры component помечаются `@Assisted`: `ComponentContext`, args/config fields, callbacks/router.
- DI зависимости передаются обычными параметрами класса.
- Component expose-ит UI только единый `StateFlow<*State>` и единый метод `onUIEvent(event)`.
- Не добавляй отдельные UI методы вроде `onRetryClick`, `onBackClick`, `onNameChanged`: они должны быть events.
- Screen component не должен знать о `StackNavigation` parent component. Он вызывает методы `router`.
- Component не содержит `@Composable View` и не наследуется от `Feature`.
- Screen/content composable вызывается из `Feature { }` в public factory или internal screen factory.
- Подробные правила state/event, `Resource -> UiState`, `UiError` и Compose connector см. в `presentation-layer.md`.

## Parent Flow Component

Flow-фича всегда имеет root component, даже если сейчас в flow один child. Root component наследуется от `BaseParentComponent<RouterType, ConfigType>`.

```kotlin
@AssistedInject
internal class ProfileRootComponent(
    @Assisted componentContext: ComponentContext,
    @Assisted private val args: ProfileArgs,
    @Assisted private val callbacks: ProfileCallbacks,
    private val detailsFeatureFactory: ProfileDetailsFeatureFactory,
    private val editFeatureFactory: ProfileEditFeatureFactory,
) : BaseParentComponent<Router, ProfileConfig>(
    initialConfiguration = ProfileConfig.Details(args.profileId),
    serializer = ProfileConfig.serializer(),
    router = Router(callbacks::goBack),
    componentContext = componentContext,
) {

    private val childRouter = object : ProfileRouter {
        override fun goBack() {
            this@ProfileRootComponent.goBack()
        }

        override fun openEdit(profileId: String) {
            navigation.pushNew(ProfileConfig.Edit(profileId))
        }
    }

    override fun createChild(
        config: ProfileConfig,
        componentContext: ComponentContext,
    ): Feature {
        return when (config) {
            is ProfileConfig.Details -> detailsFeatureFactory.create(
                componentContext = componentContext,
                profileId = config.profileId,
                router = childRouter,
            )
            is ProfileConfig.Edit -> editFeatureFactory.create(
                componentContext = componentContext,
                profileId = config.profileId,
                router = childRouter,
            )
        }
    }

    override suspend fun handleDeeplink(uri: Uri): Boolean {
        val config = parseProfileConfig(uri) ?: return false
        navigation.pushNew(config)
        return true
    }

    fun goBack() {
        navigation.pop { isSuccess ->
            if (!isSuccess) callbacks.goBack()
        }
    }

    @AssistedFactory
    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
            args: ProfileArgs,
            callbacks: ProfileCallbacks,
        ): ProfileRootComponent
    }
}
```

`ProfileRouter` может быть `fun interface`, если нужен только back, или `interface`/class с несколькими действиями.

```kotlin
internal interface ProfileRouter : Router {
    fun openEdit(profileId: String)
}
```

Правила:

- `BaseParentComponent` уже содержит `StackNavigation`, `childStack`, `handleBackButton = true` и deeplink delegation.
- Child `Feature` создается только через internal screen `*FeatureFactory.create(...)`.
- Root component не создает screen component напрямую и не знает screen graph.
- Для навигации используй `navigation.pushNew`, `navigation.replaceCurrent`, `navigation.pop`, `navigation.popTo` и другие операции Decompose stack router.
- Не мутируй `childStack` напрямую.
- Back с последнего экрана flow должен вызывать внешний callback (`callbacks.goBack()`/`BackOnlyCallback.goBack()`), если pop внутри stack невозможен.
- Root flow component остается Decompose component. Если deeplink нужно пробрасывать в активный child, public factory возвращает `DeeplinkableFeature`, который делегирует `onDeeplink` в root component.

## Internal Screen Factories

Internal `*FeatureFactory` каждого экрана flow лежит в `presentation/<screen>/navigation`, создает собственный screen graph и возвращает `Feature`.

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

Правила:

- У каждого внутреннего экрана flow есть своя internal `*FeatureFactory`.
- Screen factory получает `*ParentDependencies`, создает свой graph внутри `create(...)`, затем создает Decompose component.
- Screen graph создается на каждый вызов `create(...)`, чтобы подэкран получил собственный scope.
- Если экрану нужны screen-level bindings/providers, они находятся в `ProfileDetailsGraph.kt`, а не в root graph.

## Config and Serialization

Config описывает только навигационное состояние parent component.

```kotlin
@Serializable
internal sealed interface ProfileConfig {

    @Serializable
    data class Details(val profileId: String) : ProfileConfig

    @Serializable
    data class Edit(val profileId: String) : ProfileConfig
}
```

Правила:

- Все config variants должны быть `@Serializable`.
- В config клади только стабильные navigation args: id, enum, primitive/string value, lightweight public args.
- Не клади в config callbacks, component instances, repositories, use cases, `StateFlow`, `Resource`, `UiState`, DTO/entity/presentation models или platform objects.
- Если экрану нужен большой объект, передавай id в config, а данные получай через use case/repository.
- Config живет в impl модуле, потому что это внутренняя структура flow.

## Single Screen Feature

Single-screen фича использует тот же принцип, но без root flow component: public `*FeatureFactoryImpl` создает screen graph и screen component, затем возвращает `Feature`.

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
            ProfileScreen(
                component = component,
                modifier = modifier,
            )
        }
    }
}
```

## Compose Rendering and Animations

Parent/root composable рендерит `childStack` через Decompose Compose `ChildStack` и project animation helpers.

```kotlin
@OptIn(ExperimentalDecomposeApi::class)
@Composable
internal fun ProfileRootContent(
    component: ProfileRootComponent,
    modifier: Modifier = Modifier,
) {
    ChildStack(
        modifier = modifier,
        stack = component.childStack,
        animation = stackAnimation(
            selector = { _, _, _, _ -> defaultStackAnimator() },
            predictiveBackParams = predictiveBackParams(
                component.backHandler,
                component::goBack,
            ),
        ),
    ) { child ->
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@ChildStack,
        ) {
            child.instance.View(Modifier)
        }
    }
}
```

Правила:

- Stack animation задается в composable, а не в component.
- Используй существующие helpers из `core.navigation.animations`, если фича не требует особого перехода.
- `BaseParentComponent` отвечает за navigation state; UI layer отвечает за анимацию stack.
- Predictive back подключай через `predictiveBackParams(component.backHandler, component::goBack)` для parent components, где нужен системный back gesture.

## Shared Transition

В приложении уже есть app-level `SharedTransitionLayout` в root UI, который предоставляет:

- `LocalSharedTransitionScope`
- `LocalAnimatedVisibilityScope`

По умолчанию feature UI должна использовать эти locals, а не создавать новый `SharedTransitionLayout`.

```kotlin
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun ProfileAvatar(
    profileId: String,
    modifier: Modifier = Modifier,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    with(sharedTransitionScope) {
        Avatar(
            modifier = modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(
                    key = "profile-avatar-$profileId",
                ),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
        )
    }
}
```

Правила:

- Используй shared transition только для элементов, которые есть на source и destination экранах.
- Ключи shared content должны быть стабильными и уникальными внутри flow: включай имя фичи/сущности/id.
- Не используй index списка как key, если item может поменять позицию.
- Для переходов между экранами одного `ChildStack` используй `LocalSharedTransitionScope` + `LocalAnimatedVisibilityScope`.
- Если transition должен работать между root app screens, не создавай nested `SharedTransitionLayout` внутри фичи: он изолирует transition scope.
- Feature-local `SharedTransitionLayout` допустим только для полностью локального flow/виджета, где source и destination не должны анимироваться за пределы этой фичи. В этом случае root content фичи сам предоставляет оба locals для своего внутреннего `ChildStack`.
- Если shared transition не нужен, ограничься обычным `stackAnimation`.

## Deeplink Rules

- Публичная deeplink-фича наследуется от `DeeplinkableFeatureFactory`.
- `canHandle(uri)` отвечает только на вопрос, относится ли uri к фиче.
- `parseArgs(uri)` строит публичные `Args`.
- Внутри flow `handleDeeplink(uri)` строит internal `Config` и делает navigation action.
- `BaseParentComponent.onDeeplink(uri)` сначала дает активному child обработать deeplink, затем вызывает `handleDeeplink`, затем повторно пробрасывает deeplink активному child.

## Checklist

- В api модуле есть только одна public `*FeatureFactory` для входа в ui-фичу.
- Public `FeatureFactoryImpl.create(componentContext, args, callbacks)` создает root graph для flow или screen graph для single-screen фичи.
- Каждый внутренний экран flow имеет свою internal `*FeatureFactory`.
- Каждая internal screen `*FeatureFactory.create(...)` создает собственный screen graph через `createGraphFactory`.
- Root graph и screen graphs лежат в отдельных файлах внутри `di/`.
- Internal screen `*AppBindings` подключаются в root graph фичи, а не в `shared`.
- Graph возвращает component factory, а не готовый component singleton и не `Feature`.
- Flow-фича имеет root component на `BaseParentComponent`.
- Internal screen navigation не протекает в api модуль.
- Config variants serializable и не содержат callbacks/DI/component/state objects.
- Components не наследуются от `Feature`; `Feature { }` создается в navigation layer.
- Child components получают navigation через router/callbacks, а не через parent `StackNavigation`.
- Stack animation и shared transition настраиваются в composable layer.
- Shared transition использует project locals, кроме осознанных feature-local scopes.
