package com.lebedaliv2601.core.language.api

/**
 * Апи класс для работы с языком приложения
 */
interface LanguageApi {

    /**
     * Метод для получения текущего языка приложения
     * @return текущий язык [AppLanguage]
     */
    fun currentLanguage(): AppLanguage
}