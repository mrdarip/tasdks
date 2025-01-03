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
    fun fromEndReason(executionStatus: ExecutionStatus): String {
        return executionStatus.name
    }

    @TypeConverter
    fun toEndReason(value: String): ExecutionStatus {
        return ExecutionStatus.valueOf(value)
    }

    @TypeConverter
    fun fromIdRoute(idRoute: IDRoute): String {
        return idRoute.route.joinToString(separator = "/")
    }

    @TypeConverter
    fun toIdRoute(value: String): IDRoute {
        return IDRoute(value.split("/").filter { it.isNotEmpty() }.map { it.toLong() })
    }
}