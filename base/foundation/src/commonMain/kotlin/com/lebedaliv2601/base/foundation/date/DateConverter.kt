package com.lebedaliv2601.base.foundation.date

import com.lebedaliv2601.base.logger.L
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Конвертер между epoch-миллисекундами и датами/временем.
 */
interface DateConverter {
    /**
     * Преобразует millis в LocalDate с учетом временной зоны. Возвращает null при ошибке.
     */
    fun epochMillisToLocalDate(
        millis: Long,
        timeZone: TimeZone = DateUtils.currentTimeZone()
    ): LocalDate?

    /**
     * Преобразует millis в LocalDateTime с учетом временной зоны. Возвращает null при ошибке.
     */
    fun epochMillisToLocalDateTime(
        millis: Long,
        timeZone: TimeZone = DateUtils.currentTimeZone()
    ): LocalDateTime?

    /**
     * Преобразует LocalDate в millis (начало дня) с учетом временной зоны.
     */
    fun localDateToEpochMillis(
        date: LocalDate,
        timeZone: TimeZone = DateUtils.currentTimeZone()
    ): Long

    /**
     * Преобразует LocalDateTime в millis с учетом временной зоны.
     */
    fun localDateTimeToEpochMillis(
        dateTime: LocalDateTime,
        timeZone: TimeZone = DateUtils.currentTimeZone()
    ): Long
}

/**
 * Базовая реализация конвертера на базе kotlinx-datetime.
 */
@OptIn(ExperimentalTime::class)
class DefaultDateConverter : DateConverter {
    override fun epochMillisToLocalDate(millis: Long, timeZone: TimeZone): LocalDate? =
        try {
            Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone).date
        } catch (exception: Exception) {
            L.d(exception, "DateConverter.epochMillisToLocalDate failed. millis=$millis timeZone=$timeZone")
            null
        }

    override fun epochMillisToLocalDateTime(millis: Long, timeZone: TimeZone): LocalDateTime? =
        try {
            Instant.fromEpochMilliseconds(millis).toLocalDateTime(timeZone)
        } catch (exception: Exception) {
            L.d(exception, "DateConverter.epochMillisToLocalDateTime failed. millis=$millis timeZone=$timeZone")
            null
        }

    override fun localDateToEpochMillis(date: LocalDate, timeZone: TimeZone): Long =
        date.atStartOfDayIn(timeZone).toEpochMilliseconds()

    override fun localDateTimeToEpochMillis(dateTime: LocalDateTime, timeZone: TimeZone): Long =
        dateTime.toInstant(timeZone).toEpochMilliseconds()
}