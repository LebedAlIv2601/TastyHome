# Domain Models

## Purpose

Доменные модели описывают бизнес-сущности и значения, с которыми работает фича после преобразования data моделей и до преобразования в presentation модели.

Domain model отвечает на вопрос "что это значит для фичи?", а не "как это пришло из API?" или "как это нарисовать на экране?".

## Location

- Доменные модели лежат в `domain/model/`.
- Если доменная модель относится только к одному use case, она все равно остается в `domain/model/`, если является частью бизнес-смысла фичи.
- Если модель нужна только для UI, она должна лежать в `presentation/<screen-or-component>/model/`.
- Если модель нужна только для сериализации сети, она должна лежать в `data/model/` и называться `*DTO`.
- Если модель нужна только для базы данных, она должна лежать в data/model зоне проекта и называться `*Entity`.

## Naming

- Доменная сущность называется без технических суффиксов: `Profile`, `Recipe`, `Ingredient`.
- Value object называется по смыслу значения: `RecipeId`, `Calories`, `CookingTime`, `Money`.
- State-like доменные enum/sealed модели называются по бизнес-смыслу: `ProfileStatus`, `OrderState`.
- Не используй суффиксы `DTO`, `Entity`, `Model`, `UiModel` для domain моделей.
- Имя файла должно совпадать с главной декларацией.

## Shape Rules

- Domain модель должна быть удобной для бизнес-логики фичи.
- Domain модель не обязана повторять структуру API response или database entity.
- Domain модель не должна подстраиваться под конкретный экран.
- Nullable поля допустимы только если отсутствие значения имеет бизнес-смысл.
- Default values допустимы только если default является бизнес-правилом, а не способом скрыть плохой API response.
- Сложные примитивы лучше выражать отдельными value objects, если это снижает риск перепутать значения.

## Dependency Rules

- Domain модели не зависят от DTO, Entity, presentation моделей, Compose, Decompose, Android/iOS классов.
- Domain модели не содержат UI callbacks и не знают про навигацию.
- Domain модели не содержат repository/use case зависимости.
- Domain модели могут использовать общие domain abstractions из `base/domain`, если это соответствует существующему стилю проекта.

## Annotation Rules

- Не добавляй `@Serializable`, если модель не является частью явного domain-контракта сериализации.
- Не добавляй Room annotations (`@Entity`, `@PrimaryKey`, `@ColumnInfo`) в domain модели.
- Не добавляй UI annotations и platform annotations.

## Behavior Rules

- Простые invariants можно выражать через value objects, init-checks или factory-функции, если это уже принято в соседнем коде.
- Не добавляй форматирование для UI: строки, цвета, иконки и localized text должны появляться в presentation слое.
- Не добавляй parsing внешнего контракта: это задача data mapper'а.
- Не добавляй IO, сетевые запросы, работу с базой или DataStore.

## Correct Example

```kotlin
internal data class Profile(
    val id: ProfileId,
    val displayName: String,
    val avatarUrl: String?,
    val status: ProfileStatus,
)

internal data class ProfileId(
    val value: String,
)

internal enum class ProfileStatus {
    Active,
    Blocked,
}
```

## Incorrect Example

```kotlin
@Serializable
@Entity(tableName = "profiles")
internal data class Profile(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @SerialName("display_name")
    val displayName: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
) {
    fun titleForScreen(): String = displayName.uppercase()
}
```

Этот пример неправильный, потому что одна domain модель одновременно является DTO, Entity и UI-моделью, содержит callback и форматирование для экрана.
