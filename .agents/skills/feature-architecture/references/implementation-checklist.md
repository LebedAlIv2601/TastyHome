# Implementation Checklist

## TODO: Before Coding

- Confirm target module and layer.
- Confirm API/Impl boundary.
- Confirm dependency direction.

## TODO: While Coding

- Keep platform details in platform source sets or platform abstractions.
- Keep data, domain, and presentation models separated.
- Reuse existing project patterns.
- Keep UI communication to one `StateFlow<*State>` in property `state` and one `onUIEvent(event)` method per component.
- Map observable domain data + loading flag + `ResultStatus` to presentation `UiState<Model>` in the component/presentation layer.
- Map `Throwable`/`DataError` to `UiError` before data reaches composable functions.
- Store applied data/query state that changes main data content in repository as domain models.
- Store UI/process state in component only.
- Keep use cases stateless.

## TODO: Before Finishing

- Check Gradle dependencies.
- Check that `configureFeatureApi()` is used only in the feature `api` module and never in `impl`.
- Check that ui feature `impl` modules use `configureScreenFeature()` instead of mixing api-module Gradle helpers into `impl`.
- Check feature integration into `shared`: `shared/build.gradle.kts` must depend only on the feature `impl` module, while `FeatureBindings` includes the feature `*AppBindings` from `impl`.
- Check DI wiring.
- Check navigation contracts.
- Check that public `FeatureFactory` signatures match `FeatureFactory.create(componentContext, args, callbacks)`.
- Check that flow features use `BaseParentComponent` with serializable configs.
- Check that flow features have a root graph plus separate screen graphs/scopes.
- Check that internal screen `*FeatureFactory` creates its own screen graph.
- Check that internal screen `*AppBindings` are included in the root feature graph, not in `shared`.
- Check that stack animations/shared transitions are configured in composables, not components.
- Check that applied filters/sorting/search query/paging params that change main data content are domain models stored in repository, or in Room/DataStore if persistence is required.
- Check that repository exposes data/query state with observe/update methods and does not leak `MutableStateFlow`.
- Check that repository with in-memory data/query state is scoped with `@SingleIn(<FeatureScope>::class)` or another appropriate scope.
- Check that each screen component exposes a single immutable `state` `StateFlow`.
- Check that every UI action goes through `onUIEvent(event)`.
- Check that state contains `UiState<PresentationModel>` instead of `ResultStatus`, `Result<T>`, `Throwable`, or `DataError`.
- Check that `state` is assembled reactively from input-flow via `combine(...).stateIn(...)`, not maintained manually as mutable full-screen state.
- Check that `MutableStateFlow` inside component are only private input-flow for state assembly.
- Check that component stores only UI/process state: loading, draft inputs, dialog/sheet visibility, selected UI tab, validation and enabled flags.
- Check that draft UI input is not stored in repository until it becomes applied data/query state.
- Check that use cases do not own `MutableStateFlow`, cache or query params.
- Check that ephemeral loading flags around `scope.launch` use the project-preferred completion pattern: set the flag outside launch and reset it in `invokeOnCompletion`, unless there is a documented reason to do otherwise.
- Check that component does not catch business/data errors from use case or repository methods whose contract already returns `ResultStatus` or `Result<T>`.
- If component combines observable data, loading flag and query status, check that all three participate in the same reactive state assembly.
- Check that presentation `*ModelMapper` maps domain models to presentation models and does not create `UiState`.
- Check that every UI-facing presentation model is marked with Compose `@Immutable`.
- Check that component only prepares data for UI and does not implement business logic, cache orchestration, offline-first merge, source-of-truth decisions, or persistent fallback recovery.
- Check state/error mapping: `createUiState(...)`, `Throwable.toUiError()`, `DataError.toUiError()`, or feature-specific `UiError` mapper.
- Check that every DTO class is marked with `@Serializable`, including temporary mock-backed request/response models.
- Check repository boundaries and `ResultStatus`/`Result<T>` usage.
- For screen data flows with observable local storage, check that UI data is read from local source and remote request returns only operation status.
- For refresh methods that write remote list data into local storage, check that `runForResult` returns the fetched/mapped list, while DB save is a side effect that can fail the operation.
- Run relevant tests/static checks.

## TODO: Review Questions

- Did this change add an impl dependency outside `shared`?
- Did a `base/*` module depend on `core/*` or `features/*`?
- Did a `core/*` module depend on `features/*`?
- Did presentation depend on data?
- Did domain depend on presentation?
- Did repository return DTO, Entity, presentation model, or `UiState` outside the data layer?
- Did repository return network payload directly for screen data instead of saving it to local source and exposing observable local data?
- Did applied filters/sorting/search query/paging params end up in component or use case instead of repository?
- Did repository store loading, dialog/sheet visibility, enabled flags, colors, focus or draft UI control values?
- Did a stateful repository miss feature/screen scoping and become multiple independent instances?
- Did a use case keep its own mutable state?
- Did refresh return `Unit` from save operation inside `runForResult` instead of returning fetched/mapped payload for validation?
- Did component keep request status, loading and data outside a single reactive state assembly?
- Did component implement persistent caching or source-of-truth decisions that belong in repository/local source?
- Did component mutate the final `ScreenState` manually instead of deriving it from input-flow?
- Did a feature expose internal screen configs, child factories, components, or navigation details through its api module?
- Did a composable collect anything except the component's single `state` flow?
- Did a composable call router, use cases, repositories, or platform APIs directly?
