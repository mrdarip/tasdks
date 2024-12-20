package com.mrdarip.tasdks.data.entity

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromEndReason(endReason: EndReason): String {
        return endReason.name
    }

    @TypeConverter
    fun toEndReason(value: String): EndReason {
        return EndReason.valueOf(value)
    }
}