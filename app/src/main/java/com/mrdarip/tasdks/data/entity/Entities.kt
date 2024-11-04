package com.mrdarip.tasdks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    var name: String = "",
    val comment: String? = null, //iconEmoji can be null so later on you can know what tasks weren't given an emoji
    val iconEmoji: String? = null,
    val archived: Boolean = false,
    val createdTime: Double = System.currentTimeMillis() / 1000.0,
    val isPlaylist: Boolean = false, //for tasks that allow skipping its direct subtasks
    val waitTime: Int = 0, //In seconds //for tasks that need waiting until you can do the next task
    val allowParallelTasks: Boolean = false, //for tasks that can be done at the same time as other tasks
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

@Entity(tableName = "activators")
data class Activator(
    @PrimaryKey(autoGenerate = true) val activatorId: Long = 0,
    val comment: String? = null,
    @Embedded val repetitionRange: RepetitionRange = RepetitionRange(),
    val endDate: Int? = null, //In seconds since epoch //TODO: implement 'x' button to make it null
    val endRep: Int? = 1, //TODO: Restrict EndAfterRep so it can't be 0, should be null instead
    @ColumnInfo(defaultValue = "0") val userCancelled: Boolean = false,
    val taskToActivateId: Long,
    val createdTime: Double = System.currentTimeMillis() / 1000.0
)

/**
 * @param start the time the execution started in seconds since epoch
 * @param end the time the execution ended in seconds since epoch
 * @param endReason the reason the execution ended
 * @param activatorId activator that triggered the the root of the execution tree or null if it was a one-time execution* @param parentExecution the parent execution of the execution, null if it was the root
 * @param taskId the task that was executed
 */
@Entity(tableName = "executions")
data class Execution(
    @PrimaryKey(autoGenerate = true) val executionId: Long = 0,
    val start: Int? = null,
    val end: Int? = null,
    val endReason: EndReason,
    val activatorId: Long?,
    val parentExecution: Long?,
    val taskId: Long
)

/**
 * @param successfullyEnded if the execution was successful
 * @param killsExecution if the execution should
 */
enum class EndReason(
    val successfullyEnded: Boolean,
    val killsExecution: Boolean
) { //if successfullyEnded is true, the time is valid

    SUCCESSFULLY(
        true, false
    ), //Successfully ended

    RAN_OUT_OF_TIME(
        false, true
    ), //for when the user has to leave because of deadlines

    FORGOT_TO_STOP(
        true, false
    ), //for when the user forgot to stop the execution

    FORGOT_TO_STOP_AND_THE_NEXT_EXECUTIONS(
        false, true
    ), //for when the user forgot to stop the execution and the next executions

    DID_NOT_TRACK_IT(
        true, false
    ), //for when the user did the execution but didn't track it

    I_WAS_NOT_ABLE_TO_DO_IT(
        false, true
    ), //for when the user wasn't able to do the execution because of external reasons

    CHANGED_MY_MIND(
        false, true
    ), //for when the user changed his mind

    SKIPPED(
        false, false
    ), //for when the user skips a task that allows it. The time is valid as the user did the task and marked it as skipped

    MIXED_REASONS_UNSUCCESSFULLY(
        false, true
    ), //for when any of the above reasons are mixed resulting in an unsuccessful execution

    MIXED_REASONS_SUCCESSFULLY(
        true, false
    ) //for when any of the above reasons are mixed but resulting in a successful execution
}


@Entity(primaryKeys = ["parentId", "position"])
data class TaskTaskCR(
    val parentId: Long,
    val childId: Long,
    val position: Long
)

data class TaskWithTasks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entity = Task::class,
        entityColumn = "taskId",
        associateBy = Junction(
            value = TaskTaskCR::class,
            parentColumn = "parentId",
            entityColumn = "childId"
        )
    )
    val tasks: List<Task>
)

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}