# Domain Layer

## Purpose

Domain слой описывает бизнес-смысл фичи и служит промежуточным слоем между data и presentation.

В domain слое находятся:

- доменные модели, с которыми работает фича;
- use case классы, которые описывают пользовательские или бизнес-действия;
- минимальные domain-контракты, если они нужны для изоляции логики фичи.

Domain слой не должен знать, как данные пришли из сети, как они лежат в базе, и как они будут показаны в UI.

## Structure

```text
domain/
├── model/
│   └── Profile.kt
└── usecases/
    ├── GetProfileUseCase.kt
    ├── ObserveProfileUseCase.kt
    └── UpdateProfileUseCase.kt
```

- `model/` содержит доменные модели фичи.
- `usecases/` содержит use case классы.
- Если фиче не нужны доменные модели или use case классы, соответствующая папка не создается.

## Dependency Rules

- Domain слой не зависит от presentation слоя.
- Domain слой не зависит от DTO, Entity, RemoteDataSource, LocalDataSource, Compose, Decompose или UI-моделей.
- Из data слоя domain может знать только про `*Repository` классы, если текущая архитектура фичи держит repository в `data/`.
- Repository возвращает domain модели или `Resource<DomainModel>`, но не DTO, Entity и presentation модели.
- Domain модели не должны импортировать data или presentation модели.
- Use case может зависеть от repository и других use case, если это оправдано сценарием.

## Domain Models

Подробные правила для доменных моделей лежат в `references/domain-models.md`.

Коротко:

- доменная модель отражает бизнес-сущность, а не форму API или UI;
- модель не содержит UI callbacks, Compose-типы и presentation state;
- модель не содержит DTO/Entity annotations вроде `@Serializable`, `@Entity`, `@ColumnInfo`, если они не являются частью domain-контракта проекта;
- nullable/default значения должны отражать бизнес-смысл, а не случайную форму ответа сервера.

## Use Cases

Подробные правила для use case классов лежат в `references/usecases.md`.

Коротко:

- use case описывает одно бизнес-действие или один сценарий чтения данных;
- use case получает зависимости через constructor injection;
- use case не маппит DTO в UI-модель и не работает напрямую с RemoteDataSource/LocalDataSource;
- use case возвращает domain модели, `Resource<T>` или `Flow<Resource<T>>`, если это соответствует существующему паттерну фичи.

## Correct Example

```text
domain/
├── model/
│   ├── Profile.kt
│   └── ProfileStatus.kt
└── usecases/
    ├── GetProfileUseCase.kt
    └── ObserveProfileUseCase.kt
```

```kotlin
internal class GetProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(profileId: String): Resource<Profile> {
        return repository.getProfile(profileId)
    }
}
```

## Incorrect Example

```text
domain/
├── ProfileDTO.kt
├── ProfileEntity.kt
├── ProfileUiModel.kt
└── GetProfileUseCase.kt
```

```kotlin
internal class GetProfileUseCase(
    private val remoteDataSource: ProfileRemoteDataSource,
) {
    suspend operator fun invoke(): ProfileModel {
        return remoteDataSource.getProfile().toProfileModel()
    }
}
```

Этот пример неправильный, потому что domain слой зависит от remote data source, DTO/UI-моделей и пропускает repository/domain mapping границы.
