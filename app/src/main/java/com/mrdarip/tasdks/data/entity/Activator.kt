package com.mrdarip.tasdks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "activators")
data class Activator(
    @PrimaryKey(autoGenerate = true) val activatorId: Long = 0,
    val comment: String? = null,
    val isFavourite: Boolean = false,
    @Embedded val repetitionRange: RepetitionRange = RepetitionRange(),
    val endAfterDate: Int? = null, //In seconds since epoch //TODO: implement 'x' button to make it null
    val endAfterRepetitions: Int? = 1,
    @ColumnInfo(defaultValue = "0") val userCancelled: Boolean = false,
    val taskToActivateId: Long,
    val createdTime: Double = System.currentTimeMillis() / 1000.0
    //Todo: add created fore one-time execution boolean
) {
    fun getUnstartedExecution(): Execution {
        return Execution(
            activatorId = this.activatorId,
            taskId = this.taskToActivateId,
            endReason = EndReason.UNSTARTED,
            parentExecution = null,
            end = null,
            start = null
        )
    }
}


enum class RepetitionUnit(val isExactDate: Boolean) {
    MINUTES(false),
    HOURS(false),
    DAYS(false),
    WEEKS(false),
    MONTHS(true),
    YEARS(true)
}

data class RepetitionRange(
    val firstTimeDone: Int = -1, //In seconds since epoch
    //min max activators: when will the first repetition occur
    //from-to activators: null

    val start: Int = 0, //In seconds since epoch
    //min-max activators: min
    //from-to activators: from
    val end: Int = 0,
    //min-max activators: max
    //from-to activators: to

    val repeatsEvery: Int = 1, //how often does it repeat, mustn't be <= 0
    //min-max activators: null
    //from-to activators:
    val repetitionUnit: RepetitionUnit = RepetitionUnit.DAYS
)