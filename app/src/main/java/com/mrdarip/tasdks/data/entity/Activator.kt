package com.mrdarip.tasdks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant


@Entity(tableName = "activators")
data class Activator(
    @PrimaryKey(autoGenerate = true) val activatorId: Long = 0,
    val comment: String? = null,
    val isFavourite: Boolean = false,
    @Embedded val repetitionRange: RepetitionRange = RepetitionRange(),
    val endAfterDate: Instant? = null, //TODO: implement 'x' button to make it null
    val endAfterRepetitions: Int? = 1,
    @ColumnInfo(defaultValue = "0") val userCancelled: Boolean = false,
    val taskToActivateId: Long,
    val createdTime: Instant = Instant.now()
    //Todo: add created fore one-time execution boolean
)

enum class RepetitionUnit(val isExactDate: Boolean) {
    MINUTES(false),
    HOURS(false),
    DAYS(false),
    WEEKS(false),
    MONTHS(true),
    YEARS(true)
}

data class RepetitionRange(
    val firstTimeDone: Instant = Instant.now(), //In seconds since epoch
    //min max activators: when will the first repetition occur
    //from-to activators: null

    val start: Long = 0, //In seconds since epoch
    //min-max activators: min repetitionUnits to wait to be done
    //from-to activators: from (date epoch in milliseconds)
    val end: Long = 0,
    //min-max activators: max repetitionUnits to wait to be overdue
    //from-to activators: to (date epoch in milliseconds)

    val repeatsEvery: Int = 1, //todo: remove
    //min-max activators: null
    //from-to activators:
    val repetitionUnit: RepetitionUnit = RepetitionUnit.DAYS
)