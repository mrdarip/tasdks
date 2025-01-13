package com.mrdarip.tasdks.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant


/**
 * @param start the time the execution started in seconds since epoch
 * @param end the time the execution ended in seconds since epoch
 * @param executionStatus the reason the execution ended
 * @param activatorId activator that triggered the the root of the execution tree or null if it was a one-time execution* @param parentExecution the parent execution of the execution, null if it was the root
 * @param taskId the task that was executed, or the task that started the execution, if it doesn't come from an activator
 */

@Entity(tableName = "executions")
data class Execution(
    @PrimaryKey(autoGenerate = true) val executionId: Long = 0,
    val start: Instant? = null,  //TODO: use @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val end: Instant? = null,
    val executionStatus: ExecutionStatus = ExecutionStatus.RUNNING,
    val activatorId: Long? = null,
    val taskId: Long,
    val tasksRoute: IDRoute,
    val executionRoute: IDRoute,
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
                executionStatus = ExecutionStatus.UNSTARTED,
                activatorId = activator.activatorId,
                taskId = activator.taskToActivateId,
                tasksRoute = IDRoute(emptyList()),
                executionRoute = IDRoute(emptyList()),
                childNumber = 0
            )
        }

        fun of(taskWithActivator: TaskWithActivator): Execution {
            val task = taskWithActivator.task
            val activator = taskWithActivator.activator
            return Execution(
                start = null,
                end = null,
                executionStatus = ExecutionStatus.UNSTARTED,
                activatorId = activator?.activatorId,
                taskId = task.taskId,
                tasksRoute = IDRoute(emptyList()),
                executionRoute = IDRoute(emptyList()),
                childNumber = 0
            )
        }
    }
}

data class IDRoute(
    val route: List<Long>
) {
    fun plus(id: Long): IDRoute {
        return IDRoute(route.plus(id))
    }
}

/**
 * @param successfullyEnded if the execution was completed
 * @param killsExecution if the execution should be terminated
 * @param timeIsAccurate if the time tracking is accurate
 */

enum class ExecutionStatus(
    val successfullyEnded: Boolean,
    val killsExecution: Boolean, // Determines if the current execution halts further executions
    val timeIsAccurate: Boolean, // Indicates if the tracked time is valid
    val overwritable: Boolean // Indicates if the end reason can be overwritten with a new reason or should be converted to a mixed reasons reason
) {
    /** Execution completed successfully and time tracking is accurate */
    SUCCESS(true, false, true, true),

    /** Execution ran out of time due to external deadlines; execution terminated */
    TIME_EXPIRED(false, true, false, false),

    /** Execution was completed, but user forgot to stop it, time may be inaccurate */
    COMPLETED_WITH_FORGOTTEN_STOP(true, false, false, false),

    /** Execution not stopped by user, causing unintended continuation; execution terminated */
    FORGOTTEN_STOP_INTERRUPTS(false, true, false, false),

    /** Execution completed, but was not tracked */
    UNTRACKED_COMPLETION(true, false, false, false),

    /** Execution not completed due to external circumstances; execution terminated */
    INCOMPLETE_DUE_TO_EXTERNAL_CAUSES(false, true, false, false),

    /** Execution terminated by user changing their mind */
    USER_ABORTED(false, true, false, false),

    /** Execution skipped by user’s choice; valid as completed */
    SKIPPED(true, false, true, false),

    /** Execution skipped due to lack of time; does not halt further executions */
    SKIPPED_DUE_TO_TIME(false, false, false, false),

    /** Execution terminated due to mixed unsuccessful reasons */
    UNSUCCESSFUL_MIXED_REASONS(false, true, false, false),

    /** Execution completed successfully despite mixed reasons; time may be inaccurate */
    SUCCESSFUL_MIXED_REASONS(true, false, false, false),

    /** Execution is running */
    RUNNING(false, false, true, true),

    /** Execution was created but never started */
    UNSTARTED(false, false, false, true);

    companion object {
        fun mix(old: ExecutionStatus, new: ExecutionStatus): ExecutionStatus {
            if (old == new) return old
            if (old.overwritable) {
                return new
            } else {
                if (old.successfullyEnded && new.successfullyEnded) {
                    return SUCCESSFUL_MIXED_REASONS
                } else {
                    return UNSUCCESSFUL_MIXED_REASONS
                }
            }
        }
    }
}
