# Feature Impl Structure

## Purpose

- impl модуль служит для имплементации поведения фичи и реализации публичного контракта из api

## Naming
- модуль называется impl
- нейминг package для impl: <package>.<feature>

## Top-Level Folders

- `data/`
- `domain/`
- `di/`
- `presentation/`
- `api`

## Source Sets

- Весь код по максимуму пишется в `commonMain`
- `androidMain` и `iosMain` используются для реализации платформенного поведения

## Gradle Configuration

- В impl build.gradle.kts должен быть подключен плагин baseKmp
- Должна быть использована функция configureUiFeature(), если фича - экран или флоу экранов или ui компонент
- Если фича не содержит ui, то зависимости выбираются по необходимости

## Data Folder Structure

- Структура папки data: папка remote, папка local, папка model, папка mapper, файлы `*Repository`
  - Папка remote содержит в себе все RemoteDataSource. Если они не требуются для модуля фичи, то папка не создается
  - Папка local содержит в себе все LocalDataSource. Если они не требуются для модуля фичи, то папка не создается
  - Папка model содержит в себе все DTO и другие модели уровня data слоя. Если они не требуются для модуля фичи, то папка не создается
  - Папка mapper содержит в себе все Mapper классы для преобразования . Если они не требуются для модуля фичи, то папка не создается
  - файлы `*Repository` лежат внутри папки data на уровне с остальными папками

## DI Folder Structure

- Структура папки di: один или несколько файлов `*Graph.kt`
  - Для single-screen фичи обычно достаточно `FeatureGraph.kt`
  - Для flow-фичи создается отдельный root graph и отдельный graph на каждый внутренний экран: `FeatureRootGraph.kt`, `FeatureDetailsGraph.kt`, `FeatureEditGraph.kt`
  - Каждый файл `*Graph.kt` содержит свой DI scope, DI Graph, InternalBindings/AppBindings по необходимости, а также ParentDependencies
  - Internal screen AppBindings не подключаются в `shared`; они подключаются в root graph фичи

## Domain Folder Structure

- Структура папки domain: папка model, папка usecases
  - папка model содержит в себе все доменные модели
  - папка usecases содержит в себе все `*UseCase` классы

## Api Folder Structure and Rules

- Структура папки api: impl классы, реализающие api классы из api модуля
- Папка необязательна и нужна только в случае наличия каких-то публичных апи у модуля, не являющихся FeatureFactory
- В реализации api могут использоваться исключительно domain классы и модели. Подключать туда presentation и data модели и классы строго запрещено
- модели, описанные в api фичи, тоже считаются доменными моделями и могут использоваться в качестве полноценных моделей domain уровня без их маппинга и дублирования внтури impl модуля

## Presentation Folder Structure

- Структура папки presentation: создается подпапка на каждый отдельный компонент / экран
- Внутри подпапок presentation: папка navigation, папка composable, папка component, внутри папки component папка s&e, папка model, папка mapper
    - папка navigation root/single-screen входа содержит реализацию public FeatureFactory, интерфейс которой лежит в api
    - папка navigation внутренних экранов flow содержит internal `*FeatureFactory`, которая создает graph этого экрана
    - папка composable содержит в себе все composable функции фичи
    - папка component содержит все decompose components
    - папка s&e содержит `*State` и `*Event` для взаимодействия component и composable: component expose-ит единый `StateFlow<*State>` и принимает события через единый `onUIEvent(event)`
    - папка model содержит presentation модели данных
    - папка mapper содержит внутри себя мапперы для маппинга из domain моделей в presentation модели данных 

## Rules

- Если конкретной фиче не нужны сеть, локальное хранилище, DTO, mapper'ы, model mapper-ы или presentation модели соответствующие папки не создаются.

## Examples

- Пример правильной структуры для ui фичи с одним экраном / ui элементом:
    ```
    features/
    └── profile/
        └── impl/
            ├── build.gradle.kts
            └── src/
                └── commonMain/
                    └── kotlin/
                        └── com/
                            └── tastyhome/
                                └── profile/
                                    ├── data/
                                    │   ├── ProfileRepository.kt
                                    │   ├── local/
                                    │   │   ├── ProfileDataStore.kt
                                    │   │   └── ProfileLocalDataSource.kt
                                    │   ├── mapper/
                                    │   │   └── ProfileMapper.kt
                                    │   ├── model/
                                    │   │   ├── ProfileRequestDTO.kt
                                    │   │   └── ProfileResponseDTO.kt
                                    │   └── remote/
                                    │       └── ProfileRemoteDataSource.kt
                                    ├── di/
                                    │   └── ProfileGraph.kt
                                    ├── domain/
                                    │   ├── model/
                                    │   │   └── Profile.kt
                                    │   └── usecases/
                                    │       ├── GetProfileUseCase.kt
                                    │       └── ObserveProfileUseCase.kt
                                    └── presentation/
                                        └── profile/
                                            ├── component/
                                            │   ├── ProfileComponent.kt
                                            │   └── s&e/
                                            │       ├── ProfileEvent.kt
                                            │       └── ProfileState.kt
                                            ├── composable/
                                            │   ├── ProfileContent.kt
                                            │   └── ProfileScreen.kt
                                            ├── mapper/
                                            │   └── ProfileModelMapper.kt
                                            ├── model/
                                            │   └── ProfileModel.kt
                                            └── navigation/
                                                └── ProfileFeatureFactoryImpl.kt
    ```

- Пример правильной структуры для ui фичи с флоу экранов
    ```
    features/
    └── profile/
        └── impl/
            ├── build.gradle.kts
            └── src/
                └── commonMain/
                    └── kotlin/
                        └── com/
                            └── tastyhome/
                                └── profile/
                                    ├── data/
                                    │   ├── ProfileRepository.kt
                                    │   ├── local/
                                    │   │   ├── ProfileDataStore.kt
                                    │   │   └── ProfileLocalDataSource.kt
                                    │   ├── mapper/
                                    │   │   └── ProfileMapper.kt
                                    │   ├── model/
                                    │   │   ├── ProfileRequestDTO.kt
                                    │   │   └── ProfileResponseDTO.kt
                                    │   └── remote/
                                    │       └── ProfileRemoteDataSource.kt
                                    ├── di/
                                    │   ├── ProfileRootGraph.kt
                                    │   ├── ProfileGraph.kt
                                    │   └── ProfileEditGraph.kt
                                    ├── domain/
                                    │   ├── model/
                                    │   │   ├── ProfileEdit.kt
                                    │   │   └── Profile.kt
                                    │   └── usecases/
                                    │       ├── GetProfileUseCase.kt
                                    │       ├── EditProfileUseCase.kt
                                    │       └── ObserveProfileUseCase.kt
                                    └── presentation/
                                        ├── profileRoot/
                                        │   ├── component/
                                        │   │   ├── ProfileRootComponent.kt
                                        │   │   └── s&e/
                                        │   │       ├── RootEvent.kt
                                        │   │       └── RootState.kt
                                        │   ├── composable/
                                        │   │   └── RootContent.kt
                                        │   └── navigation/
                                        │       └── ProfileRootFeatureFactoryImpl.kt
                                        ├── profile/
                                        │   ├── component/
                                        │   │   ├── ProfileComponent.kt
                                        │   │   └── s&e/
                                        │   │       ├── ProfileEvent.kt
                                        │   │       └── ProfileState.kt
                                        │   ├── composable/
                                        │   │   ├── ProfileContent.kt
                                        │   │   └── ProfileScreen.kt
                                        │   ├── mapper/
                                        │   │   └── ProfileModelMapper.kt
                                        │   ├── model/
                                        │   │   └── ProfileModel.kt
                                        │   └── navigation/
                                        │       └── ProfileFeatureFactory.kt
                                        └── profileEdit/
                                            ├── component/
                                            │   ├── ProfileEditComponent.kt
                                            │   └── s&e/
                                            │       ├── ProfileEditEvent.kt
                                            │       └── ProfileEditState.kt
                                            ├── composable/
                                            │   ├── ProfileEditContent.kt
                                            │   └── ProfileEditScreen.kt
                                            ├── mapper/
                                            │   └── ProfileEditModelMapper.kt
                                            ├── model/
                                            │   └── ProfileEditModel.kt
                                            └── navigation/
                                                └── ProfileEditFeatureFactory.kt
    ```

- Пример правильной структуры для фичи без ui:
    ```
    features/
    └── profile/
        └── impl/
            ├── build.gradle.kts
            └── src/
                └── commonMain/
                    └── kotlin/
                        └── com/
                            └── tastyhome/
                                └── profile/
                                    ├── data/
                                    │   ├── ProfileRepository.kt
                                    │   ├── local/
                                    │   │   ├── ProfileDataStore.kt
                                    │   │   └── ProfileLocalDataSource.kt
                                    │   ├── mapper/
                                    │   │   └── ProfileMapper.kt
                                    │   ├── model/
                                    │   │   ├── ProfileRequestDTO.kt
                                    │   │   └── ProfileResponseDTO.kt
                                    │   └── remote/
                                    │       └── ProfileRemoteDataSource.kt
                                    ├── di/
                                    │   └── ProfileGraph.kt
                                    ├── domain/
                                    │   ├── model/
                                    │   │   └── Profile.kt
                                    │   └── usecases/
                                    │       ├── GetProfileUseCase.kt
                                    │       └── ObserveProfileUseCase.kt
                                    └── api/
                                        └── ProfileInfoApiImpl.kt

    ```
- Пример неправильной структуры фичи:
    ```
    features/
    └── profile/
        └── impl/
            ├── build.gradle.kts
            └── src/
                └── commonMain/
                    └── kotlin/
                        └── com/
                            └── tastyhome/
                                └── profile/
                                    ├── data/
                                    │   ├── ProfileRepository.kt
                                    │   ├── ProfileDataStore.kt // ошибка: dataStore должен лежать в local папке
                                    │   ├── local/
                                    │   │   └── ProfileLocalDataSource.kt
                                    │   ├── mapper/
                                    │   │   └── ProfileMapper.kt
                                    │   ├── model/
                                    │   │   ├── ProfileRequestDTO.kt
                                    │   │   └── ProfileResponseDTO.kt
                                    │   └── remote/
                                    │       └── ProfileRemoteDataSource.kt
                                    ├── ProfileGraph.kt // ошибка: граф должен быть внутри папки di
                                    ├── domain/
                                    │   ├── model/
                                    │   │   └── Profile.kt
                                    │   └── usecases/
                                    │       ├── GetProfileUseCase.kt
                                    │       └── ObserveProfileUseCase.kt
                                    └── presentation/
                                        └── profile/
                                            ├── component/
                                            │   ├── ProfileComponent.kt
                                            │   ├── ProfileEvent.kt // ошибка: эвенты должны лежать в s&e папке
                                            │   └── ProfileState.kt // ошибка: стейты должны лежать в s&e папке
                                            ├── composable/
                                            │   ├── ProfileContent.kt
                                            │   └── ProfileScreen.kt
                                            ├── mapper/
                                            │   └── ProfileModelMapper.kt
                                            ├── model/
                                            │   └── ProfileModel.kt
                                            └── navigation/
                                                └── ProfileFeatureFactoryImpl.kt
    ```
