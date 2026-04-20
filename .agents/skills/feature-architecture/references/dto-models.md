# DTO Models

## Purpose

DTO модели описывают внешний data-контракт: тела запросов, ответы сервера и другие сериализуемые структуры, которые приходят из удаленных источников или уходят в них.

## Location

- DTO модели лежат в `data/model/`.
- Если DTO относится только к одному RemoteDataSource, она все равно остается в `data/model/`, а не рядом с методом запроса.
- Если DTO не требуется для фичи, папка `data/model/` не создается.

## Naming

- DTO классы именуются с суффиксом `DTO`: `ProfileDTO`, `ProfileResponseDTO`, `UpdateProfileBodyDTO`.
- DTO для request body можно называть `*BodyDTO`, если это уже используется в соседнем коде фичи.
- DTO для response body можно называть `*ResponseDTO`, если ответ не является самой доменной сущностью.
- Имя файла должно совпадать с главной декларацией.

## Serialization Rules

- DTO, которые сериализуются или десериализуются через kotlinx.serialization, должны быть помечены `@Serializable`.
- Используй `@SerialName`, когда имя поля в API отличается от Kotlin naming convention.
- Nullable поля использовать только если поле реально может отсутствовать или приходить как `null`.
- Default values использовать только если это безопасный data-layer fallback, а не бизнес-правило.

## Layer Rules

- DTO не должны попадать в domain или presentation слой.
- DTO не должны использовать presentation модели, `UiState`, Compose-типы, Decompose-типы или UI callbacks.
- DTO не должны содержать бизнес-логику, форматирование для UI или вычисления состояния экрана.
- DTO могут быть преобразованы в domain модели только через mapper из `data/mapper/`.
- Repository не должен отдавать DTO наружу, если публичный контракт repository ожидает domain модель.

## Shape Rules

- DTO должны повторять форму внешнего контракта, а не форму UI.
- Не нужно делать DTO удобной для экрана ценой искажения API-контракта.
- Вложенные DTO можно выносить в отдельные файлы, если они переиспользуются или становятся крупными.

## Correct Example

```kotlin
@Serializable
internal data class ProfileDTO(
    @SerialName("id")
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
)
```

## Incorrect Example

```kotlin
@Serializable
internal data class ProfileDTO(
    val id: String,
    val displayName: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
) {
    fun titleForScreen(): String = displayName.uppercase()
}
```

Этот пример неправильный, потому что DTO содержит UI-состояние, callback и форматирование для экрана.
