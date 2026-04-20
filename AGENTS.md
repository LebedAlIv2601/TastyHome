# AGENTS.md

## Назначение
Этот файл описывает текущий технический контекст проекта `TastyHome`, чтобы агентам и разработчикам было проще делать изменения в одном стиле.

## Стек
- Язык: Kotlin Multiplatform (Kotlin `2.3.20`)
- UI: Compose Multiplatform (`1.10.3`), Material3 (`1.9.0`)
- Android: AGP `9.0.1`, minSdk `28`, target/compileSdk `36`, Java `21`
- iOS: Kotlin/Native + SwiftUI host app, minimum iOS `16`
- Навигация: Decompose (`3.5.0`) + Essenty (`2.5.0`)
- DI: Metro (`0.13.2`)
- Networking: Ktor Client (`3.4.2`) + kotlinx.serialization (`1.11.0`)
- Локальное хранение: DataStore (`1.2.1`, Preferences + typed stores), encrypted serializers
- БД: Room (`2.8.4`) + SQLite bundled driver (`2.6.2`)
- Логирование: Kermit (`2.1.0`)
- Статика/качество: Detekt (`1.23.8`, кастомная конфигурация в `codeQuality/detekt/detekt.yml`)
- Build-конвенции: included build `buildLogic` + version catalog `gradle/libs.versions.toml`

## Архитектура
Проект модульный и разделен на уровни:
- `androidApp` и `iosApp`: платформенные entry points
- `shared`: composition root приложения, корневой граф зависимостей, Root-компонент
- `base/*`: базовые кросс-фичевые абстракции (platform, network, navigation, ui, domain, presentation, localStorage, logger, foundation)
- `core/*`: переиспользуемые бизнес/инфраструктурные модули (database, network/baseClient, language, designSystem, themeManager)
- `features/*`: продуктовые фичи приложения

Ключевые архитектурные принципы:
- KMP source sets: `commonMain` + платформенные `androidMain`/`iosMain`
- Composition Root в `shared/di` (Android и iOS графы отдельно)
- API/Impl split для части модулей (например `core/language`, `core/themeManager`)
- API/Impl split для всех продуктовых модулей из папки features
- Платформенная логика инкапсулируется через `Platform` интерфейс и конкретные Android/iOS реализации
- в `base/*` модулях не должно быть никакой бизнес логики и DI
- `base/*` модули НИКОГДА не должны иметь зависимость от `core/*` или `features/*` модулей, но могут зависеть друг от друга
- `core/*` модули НИКОГДА не должны иметь зависимость от `features/*` модулей, но могут зависить от `base/*` модулей и друг от друга
- `features/*` модули могут зависеть от `core/*` или `base/*` модулей, а также могут зависеть от api модулей друг друга
- Подключать impl модули куда-то, кроме shared, строго запрещено

## Структура папок
- `androidApp/`: Android приложение (Application, Activity, Android resources)
- `iosApp/`: Xcode-проект и SwiftUI оболочка над shared-кодом
- `shared/src/commonMain`: root navigation/UI, DI контейнеры, app bootstrap
- `shared/src/androidMain`, `shared/src/iosMain`: платформенные адаптеры shared-слоя
- `base/`:
  - `foundation`: утилиты и общие расширения
  - `domain`: `Resource`, data errors, cache/resource holders
  - `presentation`: `UiState`, mapper'ы, UI error mapping
  - `navigation` + `navigation/api`: базовые компоненты и feature-контракты
  - `platform`: платформенные менеджеры (permissions, file, camera, location и т.д.)
  - `network`: общий HTTP builder/настройки
  - `localStorage`: фабрики DataStore и encryption-утилиты
  - `ui`: общие ui-utils/modifiers
  - `logger`: логирование
- `core/`:
  - `database`: Room database и migrations
  - `network/baseClient`: базовая конфигурация клиента
  - `language/api|impl`: управление языком
  - `themeManager/api|impl`: управление темой
  - `designSystem`: тема, цвета, типографика, базовые компоненты
- `buildLogic/`: кастомные Gradle plugins и общие build helpers
- `codeQuality/`: detekt конфиг
- `scripts/`: git hooks и template scripts

## Naming Conventions
Ориентируемся на правила из Detekt и текущий кодстайл:
- Packages: lower case, dot-separated (`com.tastyhome...`)
- Классы/интерфейсы/object: `UpperCamelCase`
- Функции/переменные/параметры: `lowerCamelCase`
- Константы верхнего уровня: `UPPER_SNAKE_CASE`
- Приватные поля: допускается префикс `_` (по detekt)
- Имя файла должно соответствовать главной декларации (`MatchingDeclarationName`)
- Суффиксы по роли:
  - `*Manager` для platform/service abstraction
  - `*Bindings` для DI binding containers
  - `*Graph` для DI графов внутри фичей
  - `*Scope` для DI скоупов внутри фичей
  - `*Component` для Decompose компонентов
  - `*Factory` для фабрик (DI и feature creation)
  - `*Storage`/`*DataStore` для слоя хранения
  - `*Api`/`*Impl` для контрактов и реализаций
  - `*UseCase` для юзкейсов и реализаций
  - `*Repository` для репозиториев в data слое фичи
  - `*RemoteDataSource` для походов в сеть в фиче
  - `*LocalDataSource` для походов в локальные хранилища в фиче
  - `*Mapper` для маппинга из моделей data слоя (DTO, Entity) в доменные модели
  - `*ModelMapper` для маппинга из доменных моделей (DTO, Entity) в presentation модели
  - `*DTO` DTO классы для сериализации ответа от сервера
  - `*Entity` Entity классы для бд
  - `*Dao` Dao классы для бд
  - `*Content` для верхнеуровневой Composable функции экрана в фиче
  - `*Feature` для интерфейса, имплементирующего Feature из base/navigation
  - `*FeatureImpl` для реализации интерфейса, имплементирующего Feature из base/navigation

## Используемые паттерны
- Dependency Injection (Metro):
  - `@DependencyGraph`, `@BindingContainer`, `@Provides`, `@Binds`, `@SingleIn`
  - Корневой граф создается в `shared/src/*Main/.../di`
- Component-based navigation (Decompose):
  - `BaseComponent` / `BaseParentComponent`
  - Child stack + сериализуемые `Config`
  - `Feature` / `FeatureFactory` контракты
- State modeling:
  - Domain `Resource<T>` и Presentation `UiState<T>`
  - mapper-пайплайн `Resource -> UiState`
- Platform abstraction:
  - `Platform` как единая точка доступа к платформенным менеджерам
  - отдельные Android/iOS реализации
- KMP expect/actual:
  - платформенные реализации для файлов, дат, сети, темы, language API и др.
- Builder/Configuration pattern в сети:
  - `HttpClientBuilder` + набор `HttpClientSetting`
  - базовая конфигурация расширениями (`configureBaseClient`)
- Reactive flows:
  - наблюдение состояния темы/данных через `Flow`
- Design System:
  - централизованные `MyTheme`, `MyColors`, `MyTypography`

## Практические ориентиры при добавлении кода
- Сначала выбирать существующий модуль (`base`/`core`/`shared`/`features`), а не создавать новый без необходимости
- Для кроссплатформенной логики писать в `commonMain`, платформенные детали выносить в `androidMain`/`iosMain`
- Для новых зависимостей обновлять `gradle/libs.versions.toml`
- Для нового feature-UI придерживаться Decompose-контрактов и общей модели `Feature`
- Для state/error потоков придерживаться `Resource`/`UiState` и существующих mapper'ов

## Правила описания зависимостей в build.gradle.kts файлах модулей
- Обязательно подключение плагина baseKmp
- Большую часть зависимостей можно поделючить функциями из модуля buildLogic. Если там какие-то функции не подходят, то их можно добавить напрямую через commonDependencies / androidDependencies / iosDependencies

## Скиллы
- при создании и редактировании продуктовых фичей необходимо использовать skill `feature-architecture`