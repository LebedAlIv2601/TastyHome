package com.lebedaliv2601.core.language.api

/**
 * Модель языка приложения
 * @property value кодировка языка
 */
enum class AppLanguage(val value: String) {
    Russian("ru"),
    English("en");

    companion object {
        /**
         * Метод для получения [AppLanguage] на основе переданной кодировки языка
         * Возвращает русский в случае, если переданного кода языка в списке стандартных нет
         * @param valueStr кодировка языка
         */
        fun fromValue(valueStr: String?): AppLanguage {
            return AppLanguage.entries.find { it.value == valueStr } ?: Russian
        }
    }
}