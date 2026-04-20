# Implementation Checklist

## TODO: Before Coding

- Confirm target module and layer.
- Confirm API/Impl boundary.
- Confirm dependency direction.

## TODO: While Coding

- Keep platform details in platform source sets or platform abstractions.
- Keep data, domain, and presentation models separated.
- Reuse existing project patterns.
- Keep UI communication to one `StateFlow<*State>` and one `onUIEvent(event)` method per component.
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
- Check that each screen component exposes a single immutable `stateFlow`.
- Check that every UI action goes through `onUIEvent(event)`.
- Check that state contains `UiState<PresentationModel>` instead of `Resource`, `Throwable`, or `DataError`.
- Check that presentation `*ModelMapper` maps domain models to presentation models and does not create `UiState`.
- Check state/error mapping: `Resource.toUiState(...)`, `Throwable.toUiError()`, `DataError.toUiError()`, or feature-specific `UiError` mapper.
- Check repository boundaries and `Resource<T>`/holder usage.
- Run relevant tests/static checks.

## TODO: Review Questions

- Did this change add an impl dependency outside `shared`?
- Did a `base/*` module depend on `core/*` or `features/*`?
- Did a `core/*` module depend on `features/*`?
- Did presentation depend on data?
- Did domain depend on presentation?
- Did repository return DTO, Entity, presentation model, or `UiState` outside the data layer?
- Did a feature expose internal screen configs, child factories, components, or navigation details through its api module?
- Did a composable collect anything except the component's single `stateFlow`?
- Did a composable call router, use cases, repositories, or platform APIs directly?
