package com.tastyhome.core.language.api

/**
 * Апи класс для изменения языка в андроид
 */
interface ChangeLanguageApi {
    /**
     * Метод для смены языка в приложении.
     * После вызова этого метода приложение будет перезапущено
     * @param language язык для смены
     */
    suspend fun changeLanguage(language: AppLanguage)
}