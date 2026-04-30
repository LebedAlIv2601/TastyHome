# Feature Integration

## Purpose

- Этот reference описывает, как правильно подключать продуктовую фичу в `shared`
- Цель: не дублировать wiring, не размывать API/Impl boundary и не подключать лишние модули в composition root

## Core Rule

- В `shared/build.gradle.kts` подключается только `feature impl` модуль
- `feature api` модуль отдельно в `shared` не подключается
- Причина: `impl` модуль уже обязан зависеть от своего `api` модуля через `api(projects.features.<feature>.api)`, поэтому `api` приходит в `shared` транзитивно

Правильно:

```kotlin
// shared/build.gradle.kts
commonDependencies {
    implementation(projects.features.profile.impl)
}
```

Неправильно:

```kotlin
// shared/build.gradle.kts
commonDependencies {
    implementation(projects.features.profile.api)
    implementation(projects.features.profile.impl)
}
```

Подключать одновременно `api` и `impl` в `shared` запрещено. Это создает лишнюю зависимость, дублирует intent и размывает правило "impl модуль подключается только в shared".

## What Gets Wired In Shared

При интеграции single-screen или root feature в `shared` обычно меняются:

- `settings.gradle.kts`: добавить `:features:<feature>:api` и `:features:<feature>:impl`
- `shared/build.gradle.kts`: добавить только `implementation(projects.features.<feature>.impl)`
- `shared/src/commonMain/.../FeatureBindings.kt`: подключить `<Feature>AppBindings` из `impl`
- `shared/src/commonMain/.../root/*`: использовать публичный `FeatureFactory` фичи

## Dependency Direction

- `shared` знает только про `impl` модуль как про точку входа для wiring
- `shared` получает публичный `FeatureFactory` через Metro bindings из `impl`
- `impl` реализует контракт `api`
- `api` не должен подключаться в `shared` напрямую, если только нет отдельной исключительной причины, явно описанной в задаче. Для обычной feature integration такой причины нет

## DI Rule

- В `FeatureBindings` подключаются только `*AppBindings` из `impl`
- Не нужно создавать bindings для `api` модуля в `shared`
- Не нужно вручную дублировать provider/binds для `FeatureFactory`, если они уже описаны в `<Feature>AppBindings`

Правильно:

```kotlin
@BindingContainer(
    includes = [
        ProfileAppBindings::class,
    ]
)
internal interface FeatureBindings
```

## Navigation Rule

- В root/shared code используется публичный `FeatureFactory` интерфейс из `api`
- Но сам wiring этой factory в graph обеспечивается `impl` модулем через `<Feature>AppBindings`
- Это нормальная и ожидаемая схема: использовать `api` тип на уровне кода и подключать только `impl` модуль на уровне Gradle

## Review Questions

- Подключен ли в `shared/build.gradle.kts` только `impl`, без отдельного `api`?
- Есть ли у `impl` зависимость на свой `api` модуль через `api(...)`?
- Подключен ли `<Feature>AppBindings` в `FeatureBindings`?
- Использует ли root/shared code публичный `FeatureFactory` вместо internal impl классов?
- Не продублировали ли мы wiring одного и того же feature сразу через Gradle dependency и через ручные providers?
