package com.mrdarip.tasdks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter


/**
 * @param name the name of the task
 * @param comment the comment of the task
 * @param iconEmoji the emoji of the task
 * @param archived if the task is archived
 * @param createdTime the time the task was created in seconds since epoch (unix time)
 * @param waitTime the time to wait before the task can be completed, in seconds
 * @param allowParallelTasks if the task allows parallel tasks
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    var name: String = "",
    val comment: String? = null,
    val iconEmoji: String? = null,
    val archived: Boolean = false,
    val isFavourite: Boolean = false,
    val createdTime: Double = System.currentTimeMillis() / 1000.0,
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
    val isFavourite: Boolean = false,
    @Embedded val repetitionRange: RepetitionRange = RepetitionRange(),
    val endAfterDate: Int? = null, //In seconds since epoch //TODO: implement 'x' button to make it null
    val endAfterRepetitions: Int? = 1,
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
) {
    fun isRunning(): Boolean {
        return end == null
    }
}

/**
 * @param successfullyEnded if the execution was completed
 * @param killsExecution if the execution should be terminated
 * @param timeIsAccurate if the time tracking is accurate
 */

enum class EndReason(
    val successfullyEnded: Boolean,
    val killsExecution: Boolean, // Determines if the current execution halts further executions
    val timeIsAccurate: Boolean  // Indicates if the tracked time is valid
) {
    /** Execution completed successfully and time tracking is accurate */
    SUCCESS(true, false, true),

    /** Execution ran out of time due to external deadlines; execution terminated */
    TIME_EXPIRED(false, true, false),

    /** Execution was completed, but user forgot to stop it, time may be inaccurate */
    COMPLETED_WITH_FORGOTTEN_STOP(true, false, false),

    /** Execution not stopped by user, causing unintended continuation; execution terminated */
    FORGOTTEN_STOP_INTERRUPTS(false, true, false),

    /** Execution completed, but was not tracked */
    UNTRACKED_COMPLETION(true, false, false),

    /** Execution not completed due to external circumstances; execution terminated */
    INCOMPLETE_DUE_TO_EXTERNAL_CAUSES(false, true, false),

    /** Execution terminated by user changing their mind */
    USER_ABORTED(false, true, false),

    /** Execution skipped by user’s choice; valid as completed */
    SKIPPED(true, false, true),

    /** Execution skipped due to lack of time; does not halt further executions */
    SKIPPED_DUE_TO_TIME(false, false, false),

    /** Execution terminated due to mixed unsuccessful reasons */
    UNSUCCESSFUL_MIXED_REASONS(false, true, false),

    /** Execution completed successfully despite mixed reasons; time may be inaccurate */
    SUCCESSFUL_MIXED_REASONS(true, false, false)
}

@TypeConverter
fun fromEndReason(endReason: EndReason): String {
    return endReason.name
}

@TypeConverter
fun toEndReason(value: String): EndReason {
    return EndReason.valueOf(value)
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