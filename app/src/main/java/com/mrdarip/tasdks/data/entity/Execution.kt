package com.mrdarip.tasdks.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


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
    val start: Int? = null,  //TODO: use @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val end: Int? = null,
    val endReason: EndReason = EndReason.RUNNING, //TODO: rename to something that doesn't imply that the execution was completed, like "runningStatus"
    val activatorId: Long? = null,
    val parentExecution: Long? = null,
    val taskId: Long, //Could be removed as it is redundant as it is obtainable from the activator
    val routeIds: idRoute,
    val childNumber: Int
) {
    fun isStarted(): Boolean {
        return start != null
    }

    companion object {
        fun of(activator: Activator): Execution {
            return Execution(
                start = null,
                end = null,
                endReason = EndReason.UNSTARTED,
                activatorId = activator.activatorId,
                parentExecution = null,
                taskId = activator.taskToActivateId,
                routeIds = idRoute(emptyList()),
                childNumber = 0
            )
        }

        fun of(task: Task): Execution {
            return Execution(
                start = null,
                end = null,
                endReason = EndReason.UNSTARTED,
                activatorId = null,
                parentExecution = null,
                taskId = task.taskId,
                routeIds = idRoute(emptyList()),
                childNumber = 0
            )
        }
    }
}

data class idRoute(
    val route: List<Long>
)

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
    SUCCESSFUL_MIXED_REASONS(true, false, false),

    /** Execution is running */
    RUNNING(false, false, true),

    /** Execution was created but never started */
    UNSTARTED(false, false, false)
}

