# DataStore

## Purpose

DataStore создается для локального хранения примитивных данных (строк, boolean значений и тд)

## Creation Rules

- для создания DataStore необходимо использовать DataStoreFactory из base/localStorage
- Кладется в папку local
- В DI его создание выполняется в AppBindings с SingleIn(AppScope::class), в InternalBindings помещать его нельзя
- Зависимость прокидывается через ParentDependencies
- класс DataStore должен быть internal

## Storage Model Rules

- Класс с датастором внутри используется с окончанием `*DataStore`
- Название преференса для датастора формируется с окончанием "_tag"
- Преференс кладется в одном файле рядом с DataStore в виде переменной c названием только большими буквами и окончанием `*_PREFERENCE`

## Examples

- Правильный пример:
    ```kotlin
    // файл в папке local
    val THEME_TAG_PREFERENCE = stringPreferencesKey("theme_tag")

    internal class ThemeDataStore(
        dataStore: DataStore<Preferences>
    ) : DataStore<Preferences> by dataStore
  
    // Файл в папке DI
    @Inject
    data class ParentDependencies(
        // другие внешние зависимости
        val dataStore: ThemeDataStore
    )
  
    @BindingContainer
    abstract class ThemeManagerAppBindings {
        companion object {
            @SingleIn(AppScope::class)
            @Provides
            internal fun provideStorage(
                factory: DataStoreFactory,
            ): ThemeDataStore {
                return ThemeDataStore(factory.create(filename = "theme_store"))
            }
        }
    }
    ```
- Неправильный пример:
    ```kotlin
    // файл в папке local
    // ошибка: название переменной и тега внутри не соответствует правилам нейминга
    val theme = stringPreferencesKey("theme")

    // ошибка: класс не internal и не соответствует правилам нейминга
    class ThemeStorage(
        dataStore: DataStore<Preferences>
    ) : DataStore<Preferences> by dataStore
  
    // Файл в папке DI
    // ошибка: должен использоваться DataStoreFactory из base/localStorage, а не напрямую из библиотеки
    import androidx.datastore.core.DataStoreFactory
    @BindingContainer
    // Ошибка: datastore должен провайдится через AppBindings
    internal abstract class ThemeManagerInternalBindings {
        companion object {
            // ошибка: скоуп должен быть AppScope
            @SingleIn(ThemeScope::class)
            @Provides
            internal fun provideStorage(
                factory: DataStoreFactory,
            ): ThemeDataStore {
                return ThemeDataStore(factory.create(filename = "theme_store"))
            }
        }
    }
    ```
