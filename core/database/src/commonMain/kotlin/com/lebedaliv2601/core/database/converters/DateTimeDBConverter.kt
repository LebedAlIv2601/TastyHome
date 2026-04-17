package com.lebedaliv2601.core.database.converters

import androidx.room.TypeConverter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class DateTimeDBConverter {
    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun fromDate(date: Instant?): String? {
        return date?.toString()
    }

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun toDate(value: String?): Instant? {
        return value?.let { Instant.parse(it) }
    }
}