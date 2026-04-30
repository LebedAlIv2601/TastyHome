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
- Map domain `Resource<T>` to presentation `UiState<Model>` in the component/presentation layer.
- Map `Throwable`/`DataError` to `UiError` before data reaches composable functions.

## TODO: Before Finishing

- Check Gradle dependencies.
- Check DI wiring.
- Check navigation contracts.
- Check that public `FeatureFactory` signatures match `FeatureFactory.create(componentContext, args, callbacks)`.
- Check that flow features use `BaseParentComponent` with serializable configs.
- Check that flow features have a root graph plus separate screen graphs/scopes.
- Check that internal screen `*FeatureFactory` creates its own screen graph.
- Check that internal screen `*AppBindings` are included in the root feature graph, not in `shared`.
- Check that stack animations/shared transitions are configured in composables, not components.
- Check that each screen component exposes a single immutable `state` `StateFlow`.
- Check that every UI action goes through `onUIEvent(event)`.
- Check that state contains `UiState<PresentationModel>` instead of `Resource`, `Throwable`, or `DataError`.
- Check that `state` is assembled reactively from input-flow via `combine(...).stateIn(...)`, not maintained manually as mutable full-screen state.
- Check that `MutableStateFlow` inside component are only private input-flow for state assembly.
- Check that presentation `*ModelMapper` maps domain models to presentation models and does not create `UiState`.
- Check that every UI-facing presentation model is marked with Compose `@Immutable`.
- Check that component only prepares data for UI and does not implement business logic, cache orchestration, offline-first merge, source-of-truth decisions, or persistent fallback recovery.
- Check state/error mapping: `Resource.toUiState(...)`, `Throwable.toUiError()`, `DataError.toUiError()`, or feature-specific `UiError` mapper.
- Check that every DTO class is marked with `@Serializable`, including temporary mock-backed request/response models.
- Check repository boundaries and `Resource<T>`/holder usage.
- For offline-first SSOT flows with observable local storage, check that `ResourceHolder + CacheHolder(local source)` was considered before moving refresh/error orchestration into presentation.
- Run relevant tests/static checks.

## TODO: Review Questions

- Did this change add an impl dependency outside `shared`?
- Did a `base/*` module depend on `core/*` or `features/*`?
- Did a `core/*` module depend on `features/*`?
- Did presentation depend on data?
- Did domain depend on presentation?
- Did repository return DTO, Entity, presentation model, or `UiState` outside the data layer?
- Did an offline-first repository bypass `ResourceHolder + CacheHolder(local source)` and push refresh/error merge logic into the component without a strong reason?
- Did component rebuild cached fallback/error merge logic that should already be owned by repository or use case?
- Did component mutate the final `ScreenState` manually instead of deriving it from input-flow?
- Did a feature expose internal screen configs, child factories, components, or navigation details through its api module?
- Did a composable collect anything except the component's single `state` flow?
- Did a composable call router, use cases, repositories, or platform APIs directly?
