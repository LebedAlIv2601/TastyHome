# Feature API Module

## Purpose

- api модуль создается для предоставления внешнего контракта фичи другим фичам для ее открытия / вызова / использования

## Required Structure

- В api модуле находится интерфейс, имплементирующий FeatureFactory из base/navigation, со всеми нужными моделями (Callbacks, Args и тд)
- нейминг package для api: <package>.<feature>.api
- также в api модуле могут находиться интерфейсы, реализация которых будет лежать в impl
- если фича не предполагает ui, то создавать FeatureFactory не нужно, достаточно api интерфейса (как в core:themeManager:api)

## Dependencies

- В build.gradle.kts api модуля подключается baseKmp плагин и вызывается функция configureFeatureApi()
- остальные зависимости в api модуле по необходимости
- в api модуль фичи нельзя подключать impl модуль. Также в него нельзя подключать api модули других фичей
- в api модуль можно подключать base и core модули (если core модули разделены на api и impl, то только api)

## Examples

- Правильная структура api модуля для ui фичи:
    ```
    features/
    └── profile/
        └── api/
            ├── build.gradle.kts
            └── src/
                └── commonMain/
                    └── kotlin/
                        └── com/
                            └── tastyhome/
                                └── profile/
                                    └── api/
                                        ├── ProfileFeature.kt
                                        ├── ProfileArgs.kt
                                        └── ProfileCallbacks.kt
    ```

  В такой структуре `ProfileFeature.kt` содержит публичный контракт фичи, который имплементирует `FeatureFactory` из `base/navigation`. `ProfileArgs.kt` и `ProfileCallbacks.kt` содержат только публичные модели, нужные вызывающему коду для открытия фичи и получения результата.

- Правильная структура api модуля для фичи без ui:
    ```
    features/
    └── user-session/
        └── api/
            ├── build.gradle.kts
            └── src/
                └── commonMain/
                    └── kotlin/
                        └── com/
                            └── tastyhome/
                                └── usersession/
                                    └── api/
                                        ├── UserSessionApi.kt
                                        └── UserSessionState.kt
    ```

  Если фича не открывает экран или flow экранов, `FeatureFactory` создавать не нужно. В api модуле остается только публичный интерфейс и публичные модели, которые нужны другим модулям.

- Неправильная структура api модуля:
    ```
    features/
    └── profile/
        └── api/
            ├── build.gradle.kts
            └── src/
                └── commonMain/
                    └── kotlin/
                        └── com/
                            └── tastyhome/
                                └── profile/
                                    ├── ProfileFeatureImpl.kt
                                    ├── data/
                                    │   ├── ProfileRepository.kt
                                    │   └── remote/
                                    │       └── ProfileRemoteDataSource.kt
                                    ├── domain/
                                    │   └── usecases/
                                    │       └── GetProfileUseCase.kt
                                    └── presentation/
                                        └── profile/
                                            ├── component/
                                            │   └── DefaultProfileComponent.kt
                                            └── composable/
                                                └── ProfileContent.kt
    ```

  Такая структура неправильная, потому что:

  - package не заканчивается на `.api`
  - в api модуле лежит реализация `ProfileFeatureImpl`
  - в api модуле лежат `data`, `domain` и `presentation` слои
  - в api модуле лежат `Repository`, `RemoteDataSource`, `UseCase`, `Component` и `Composable`
  - публичный контракт смешан с внутренней реализацией фичи
  - такой api модуль вынудит другие модули зависеть от деталей impl
