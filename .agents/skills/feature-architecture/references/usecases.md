# Use Cases

## Purpose

Use case описывает одно бизнес-действие, один сценарий чтения данных или одну операцию над состоянием фичи.

Use case нужен, когда:

- действие используется компонентом/несколькими компонентами;
- действие содержит бизнес-условия;
- нужно скрыть repository contract от presentation;
- нужно стабилизировать сценарий для тестирования;
- несколько источников данных или операций должны выглядеть как один бизнес-сценарий.

Use case не нужен, если он только проксирует один метод без добавления смысла, а соседний код фичи не использует такой стиль.

## Location

- Use case классы лежат в `domain/usecases/`.
- Один файл содержит один основной use case.
- Если use case отсутствуют в фиче, папка `domain/usecases/` не создается.

## Naming

- Use case именуется с суффиксом `UseCase`: `GetProfileUseCase`, `ObserveProfileUseCase`, `UpdateProfileUseCase`.
- Имя начинается с глагола: `Get`, `Observe`, `Update`, `Delete`, `Create`, `Validate`, `Calculate`.
- Для наблюдения `Flow` используй `Observe*UseCase`.
- Для одноразового чтения используй `Get*UseCase` или другой точный глагол.
- Имя файла должно совпадать с главной декларацией.

## Constructor Rules

- Зависимости передаются через primary constructor.
- Repository можно инжектить напрямую, если repository лежит в `data/` и является разрешенной зависимостью domain слоя в текущей архитектуре проекта.
- Не инжекть `RemoteDataSource`, `LocalDataSource`, `DataStore`, `HttpClient`, `AppDatabase` или platform managers напрямую.
- Не инжекть UI callbacks, navigation callbacks или Compose state.
- Если проектный стиль требует Metro constructor injection, добавляй `@Inject` по аналогии с соседними use case.

## Method Rules

- Основной метод use case называется `operator fun invoke`.
- Use case с одноразовой async операцией может быть `suspend`.
- Use case, который возвращает `Flow`, обычно не обязан быть `suspend`.
- Параметры use case должны быть domain primitives/value objects или args-команды, а не DTO/UI models.
- Не передавай в use case Composable state, callbacks или Decompose компоненты.

## Return Rules

- Use case возвращает domain модель, domain value, `Resource<T>`, `Flow<T>` или `Flow<Resource<T>>` в зависимости от существующего паттерна фичи.
- Use case не возвращает DTO, Entity, presentation model, `UiState` или Compose state.
- Ошибки должны оставаться в domain/data error модели проекта, а не превращаться в текст для UI.
- UI error mapping выполняется в presentation слое.

## Behavior Rules

- Use case может содержать бизнес-условия, валидацию, выбор repository метода, комбинирование domain потоков.
- Use case не должен выполнять HTTP-запросы напрямую.
- Use case не должен читать/писать DataStore или базу напрямую.
- Use case не должен форматировать строки для экрана.
- Use case не должен управлять навигацией.
- Use case не должен знать про lifecycle компонента.

## Correct Example

```kotlin
internal class ObserveProfileUseCase(
    private val repository: ProfileRepository,
) {
    operator fun invoke(profileId: ProfileId): Flow<Resource<Profile>> {
        return repository.observeProfile(profileId)
    }
}
```

```kotlin
internal class UpdateProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(profile: Profile): Resource<Unit> {
        return repository.updateProfile(profile)
    }
}
```

## Incorrect Example

```kotlin
internal class GetProfileUseCase(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val mapper: ProfileModelMapper,
) {
    suspend operator fun invoke(onOpenProfile: () -> Unit): UiState<ProfileModel> {
        val dto = remoteDataSource.getProfile()
        val model = mapper.toModel(dto)
        return UiState.Data(model)
    }
}
```

Этот пример неправильный, потому что use case зависит от RemoteDataSource и presentation mapper, принимает UI callback и возвращает `UiState<ProfileModel>`.
