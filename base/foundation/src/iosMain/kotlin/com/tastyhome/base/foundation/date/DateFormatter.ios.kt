package com.tastyhome.base.foundation.date

import com.tastyhome.base.logger.L
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.systemTimeZone
import kotlin.time.ExperimentalTime

/**
 * iOS-реализация локализованного форматтера.
 * Форматирование: LocalDate/LocalDateTime → Instant → [toNSDate] → NSDateFormatter.
 * Парсинг: NSDateFormatter → NSDate → [toKotlinInstant] → [toLocalDateTime].
 */
private class IosDateFormatter(private val localeTag: String) : DateFormatter {

    @OptIn(ExperimentalTime::class)
    override fun format(date: LocalDate, pattern: DatePattern): String? =
        try {
            val formatter = formatter(pattern)
            val nsDate = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toNSDate()
            formatter.stringFromDate(nsDate)
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.format(LocalDate) failed. pattern=${pattern.value} localeTag=$localeTag"
            )
            null
        }

    @OptIn(ExperimentalTime::class)
    override fun format(dateTime: LocalDateTime, pattern: DatePattern): String? =
        try {
            val formatter = formatter(pattern)
            val nsDate = dateTime.toInstant(TimeZone.currentSystemDefault()).toNSDate()
            formatter.stringFromDate(nsDate)
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.format(LocalDateTime) failed. pattern=${pattern.value} localeTag=$localeTag"
            )
            null
        }

    @OptIn(ExperimentalTime::class)
    override fun parseLocalDate(value: String, pattern: DatePattern): LocalDate? =
        try {
            val formatter = formatter(pattern)
            val nsDate = formatter.dateFromString(value) ?: return null
            nsDate.toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault()).date
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.parseLocalDate failed. pattern=${pattern.value} value=$value localeTag=$localeTag"
            )
            null
        }

    @OptIn(ExperimentalTime::class)
    override fun parseLocalDateTime(value: String, pattern: DatePattern): LocalDateTime? =
        try {
            val formatter = formatter(pattern)
            val nsDate = formatter.dateFromString(value) ?: return null
            nsDate.toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault())
        } catch (exception: Exception) {
            L.d(
                exception,
                "DateFormatter.parseLocalDateTime failed. pattern=${pattern.value} value=$value localeTag=$localeTag"
            )
            null
        }

    private fun formatter(pattern: DatePattern): NSDateFormatter {
        val formatter = NSDateFormatter()
        formatter.dateFormat = pattern.value
        formatter.locale = NSLocale(localeIdentifier = localeTag)
        formatter.timeZone = NSTimeZone.systemTimeZone
        return formatter
    }
}

/**
 * Фабрика платформенной реализации (iOS).
 */
actual fun platformDateFormatter(localeTag: String): DateFormatter =
    IosDateFormatter(localeTag)