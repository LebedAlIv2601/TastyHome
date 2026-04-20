# LocalDataSource

## Purpose

- LocalDataSource создается для работы с бд или датасторами

## Creation Rules

- В LocalDataSource инжектятся AppDatabase и DataStore-ы по необходимости
- Иногда там может производиться работа с файлами через FileManager из base/platform
- Один метод - одно действие с одним источником данных
- Один LocalDataSource - один источник данных

## Examples

- Правильный пример:
    ```kotlin
    internal class ThemeLocalDataSource(
        private val themeDataStore: ThemeDataStore
    ) {
        suspend fun saveTheme(theme: String) {
            withContext(MyDispatchers.IO) {
                themeDataStore.edit { it[THEME_TAG_PREFERENCE] = theme }
            }
        }

        fun observeTheme(default: String): Flow<String> {
            return themeDataStore.data.map { it[THEME_TAG_PREFERENCE] ?: default }
        }
    }
    ```
- Неравильный пример:
    ```kotlin
    internal class ThemeLocalDataSource(
        // ошибка: должен быть только один источник данных на один LocalDataSource
        private val themeDataStore: ThemeDataStore,
        private val database: AppDatabase
    ) {
        suspend fun saveTheme(theme: String): Flow<String> {
            // ошибка: в одном методе должен быть только один запрос к локальному источнику данных
            withContext(MyDispatchers.IO) {
                themeDataStore.edit { it[THEME_TAG_PREFERENCE] = theme }
            }
            return themeDataStore.data.map { it[THEME_TAG_PREFERENCE] ?: default }
        }
    }
    ```