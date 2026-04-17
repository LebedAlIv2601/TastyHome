package com.lebedaliv2601.base.foundation.date

import com.lebedaliv2601.base.logger.L
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Android-реализация локализованного форматтера.
 * Конвертация kotlinx ↔ java.time через [toJavaLocalDate]/[toKotlinLocalDate] и аналоги для LocalDateTime.
 */
private class AndroidDateFormatter(private val localeTag: String) : DateFormatter {
    private val locale: Locale
        get() = Locale.forLanguageTag(localeTag)

    override fun format(date: LocalDate, pattern: DatePattern): String? =
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern.value, locale)
            formatter.format(date.toJavaLocalDate())
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.format(LocalDate) failed. " +
                    "pattern=${pattern.value} localeTag=$localeTag"
            )
            null
        }

    override fun format(dateTime: LocalDateTime, pattern: DatePattern): String? =
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern.value, locale)
            formatter.format(dateTime.toJavaLocalDateTime())
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.format(LocalDateTime) failed. pattern=${pattern.value} localeTag=$localeTag"
            )
            null
        }

    override fun parseLocalDate(value: String, pattern: DatePattern): LocalDate? =
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern.value, locale)
            java.time.LocalDate.parse(value, formatter).toKotlinLocalDate()
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.parseLocalDate failed. pattern=${pattern.value} value=$value localeTag=$localeTag"
            )
            null
        }

    override fun parseLocalDateTime(value: String, pattern: DatePattern): LocalDateTime? =
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern.value, locale)
            java.time.LocalDateTime.parse(value, formatter).toKotlinLocalDateTime()
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.parseLocalDateTime failed. pattern=${pattern.value} value=$value localeTag=$localeTag"
            )
            null
        }
}

/**
 * Фабрика платформенной реализации (Android).
 */
actual fun platformDateFormatter(localeTag: String): DateFormatter =
    AndroidDateFormatter(localeTag)