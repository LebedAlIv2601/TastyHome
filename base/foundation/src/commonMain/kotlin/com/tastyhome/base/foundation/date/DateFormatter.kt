package com.tastyhome.base.foundation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Базовый форматтер дат/времени.
 */
interface DateFormatter {
    /**
     * Форматирует дату по типизированному паттерну. Возвращает null при ошибке.
     */
    fun format(date: LocalDate, pattern: DatePattern): String?

    /**
     * Форматирует дату и время по типизированному паттерну. Возвращает null при ошибке.
     */
    fun format(dateTime: LocalDateTime, pattern: DatePattern): String?

    /**
     * Парсит строку в LocalDate или null при ошибке.
     */
    fun parseLocalDate(value: String, pattern: DatePattern): LocalDate?

    /**
     * Парсит строку в LocalDateTime или null при ошибке.
     */
    fun parseLocalDateTime(value: String, pattern: DatePattern): LocalDateTime?
}

/**
 * Реализация, делегирующая на платформенный форматтер.
 * Локаль задаётся при создании и провайдится через DI.
 *
 * @param localeTag ISO 639-1 тег локали (например, "ru").
 */
class DefaultDateFormatter(localeTag: String) : DateFormatter {
    private val platformFormatter = platformDateFormatter(localeTag)

    override fun format(date: LocalDate, pattern: DatePattern): String? =
        platformFormatter.format(date, pattern)

    override fun format(dateTime: LocalDateTime, pattern: DatePattern): String? =
        platformFormatter.format(dateTime, pattern)

    override fun parseLocalDate(value: String, pattern: DatePattern): LocalDate? =
        platformFormatter.parseLocalDate(value, pattern)

    override fun parseLocalDateTime(value: String, pattern: DatePattern): LocalDateTime? =
        platformFormatter.parseLocalDateTime(value, pattern)
}

/**
 * Создаёт платформенную реализацию локализованного форматтера.
 * Локаль передаётся при создании (ISO 639-1, например "ru")
 */
expect fun platformDateFormatter(localeTag: String): DateFormatter