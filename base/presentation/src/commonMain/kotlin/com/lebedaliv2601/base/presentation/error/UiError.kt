package com.lebedaliv2601.base.presentation.error

/**
 * Интерфейс для ошибок. является моделью для ui слоя
 */
interface UiError

/**
 * Ошибка отсутствия интернета
 */
class NoInternetUiError : UiError

/**
 * Ошибка пустых данных
 */
class EmptyDataUiError : UiError

/**
 * Неизвестная ошибка
 */
class UnknownUiError : UiError