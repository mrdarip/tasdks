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

    @TypeConverter
    fun fromIdRoute(idRoute: idRoute): String {
        return idRoute.route.joinToString(separator = "/")
    }

    @TypeConverter
    fun toIdRoute(value: String): idRoute {
        return idRoute(value.split("/").filter { it.isNotEmpty() }.map { it.toLong() })
    }
}