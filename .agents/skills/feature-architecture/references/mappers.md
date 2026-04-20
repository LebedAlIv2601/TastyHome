# Mappers

## Purpose

Mapper'ы преобразуют модели между слоями и не дают DTO, Entity, domain и presentation моделям протекать за границы своего слоя.

## Location

- Data mapper'ы лежат в `data/mapper/`.
- Presentation mapper'ы лежат в `presentation/<screen-or-component>/mapper/`.
- Если mapper'ы не требуются для фичи, соответствующая папка `mapper/` не создается.

## Naming

- Mapper из data моделей в domain модели называется `*Mapper`: `ProfileMapper`.
- Mapper из domain моделей в presentation модели называется `*ModelMapper`: `ProfileModelMapper`.
- Имя файла должно совпадать с главной декларацией.

## Dependency Rules

- Data mapper может знать про data модели и domain модели.
- Data mapper не должен знать про presentation модели, Compose, Decompose или UI callbacks.
- Presentation mapper может знать про domain модели и presentation модели.
- Presentation mapper не должен знать про DTO, Entity, RemoteDataSource, LocalDataSource или Repository.
- Domain слой не содержит mapper'ы в presentation или data модели.

## Behavior Rules

- Mapper должен быть чистым преобразованием без IO, сетевых запросов, записи в хранилище и побочных эффектов.
- Mapper не должен быть `suspend`, если внутри нет реальной асинхронной операции. В нормальном случае mapper не бывает `suspend`.
- Mapper не должен скрывать ошибки внешнего контракта молчаливыми бизнес-defaults.
- Nullable значения обрабатываются явно: либо маппятся в nullable domain поле, либо превращаются в domain fallback, если такой fallback является правилом фичи.
- Списки маппятся отдельными helper-методами только если это уменьшает дублирование.
- Если mapper нужен через DI, добавляй constructor injection в стиле проекта. Если mapper используется локально и прост, можно оставить чистые функции/extension внутри mapper-файла, если это не ломает текущий стиль фичи.

## Direction Rules

- Для network response основной путь: `DTO -> domain`.
- Для request body допустим путь: `domain/command args -> DTO`.
- Для database основной путь: `Entity -> domain` и при необходимости `domain -> Entity`.
- Для UI основной путь: `domain -> presentation model`.

## Correct Data Mapper Example

```kotlin
internal class ProfileMapper {
    fun toDomain(dto: ProfileDTO): Profile {
        return Profile(
            id = dto.id,
            displayName = dto.displayName,
            avatarUrl = dto.avatarUrl,
        )
    }
}
```

## Correct Presentation Mapper Example

```kotlin
internal class ProfileModelMapper {
    fun toModel(profile: Profile): ProfileModel {
        return ProfileModel(
            title = profile.displayName,
            avatarUrl = profile.avatarUrl,
        )
    }
}
```

## Incorrect Example

```kotlin
internal class ProfileModelMapper {
    fun toModel(dto: ProfileDTO): ProfileModel {
        return ProfileModel(
            title = dto.displayName.uppercase(),
            avatarUrl = dto.avatarUrl,
        )
    }
}
```

Этот пример неправильный, потому что presentation mapper зависит от DTO и пропускает domain слой.
