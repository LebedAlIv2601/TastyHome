package com.tastyhome.base.foundation.date

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

private const val SEC_IN_MIN = 60

/**
 * Утилиты для работы с датой/временем.
 */
object DateUtils {
    /**
     * Возвращает текущий момент времени.
     */
    @OptIn(ExperimentalTime::class)
    fun now(): Instant = Clock.System.now()

    /**
     * Возвращает текущий момент времени в миллисекундах.
     */
    @OptIn(ExperimentalTime::class)
    fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

    /**
     * Возвращает системную временную зону.
     */
    fun currentTimeZone(): TimeZone = TimeZone.currentSystemDefault()

    /**
     * Текущая дата и время в системной временной зоне.
     */
    @OptIn(ExperimentalTime::class)
    fun currentLocalDateTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    /**
     * Текущая дата и время c в вашей временной зоной
     */
    @OptIn(ExperimentalTime::class)
    fun currentLocalDateTimeTZ(timeZone: TimeZone): LocalDateTime =
        Clock.System.now().toLocalDateTime(timeZone)

    /**
     * Текущая дата и время в UTC.
     */
    @OptIn(ExperimentalTime::class)
    fun currentLocalDateTimeUtc(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.UTC)

    /**
     * Возвращает длительность между двумя датами/временем.
     */
    @OptIn(ExperimentalTime::class)
    fun durationBetween(
        start: LocalDateTime,
        end: LocalDateTime,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): Duration =
        end.toInstant(timeZone) - start.toInstant(timeZone)

    /**
     * Возвращает количество минут между двумя датами/временем.
     */
    @OptIn(ExperimentalTime::class)
    fun minutesBetween(
        start: LocalDateTime,
        end: LocalDateTime,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): Long =
        (end.toInstant(timeZone) - start.toInstant(timeZone)).inWholeMinutes

    /**
     * Сравнивает две даты по календарному дню.
     */
    fun isSameDay(first: LocalDate?, second: LocalDate?): Boolean =
        first != null && second != null && first == second

    /**
     * Возвращает дату-время без времени (00:00:00.000).
     */
    fun LocalDateTime.withoutTime(): LocalDateTime =
        LocalDateTime(date, LocalTime(0, 0, 0, 0))

    /**
     * Форматирует миллисекунды в строку вида MM:SS.
     */
    fun Long.formatTime(): String {
        val duration = toDuration(DurationUnit.MILLISECONDS)
        val minutes = duration.inWholeMinutes
        val seconds = duration.inWholeSeconds % SEC_IN_MIN
        return minutes.twoDigits() + ":" + seconds.twoDigits()
    }

    private fun Long.twoDigits(): String = this.toString().padStart(2, '0')
}