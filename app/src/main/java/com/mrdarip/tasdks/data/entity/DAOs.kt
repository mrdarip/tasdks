package com.mrdarip.tasdks.data.entity


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

class DAOs {
    @Dao
    interface TaskDAO {
        @Upsert
        suspend fun upsert(task: Task)

        @Insert
        suspend fun insert(task: Task): Long


        @Delete
        fun delete(task: Task)

        @Query("SELECT * FROM tasks WHERE NOT archived")
        fun getActive(): Flow<List<Task>>

        @Query(
            """
        SELECT * FROM tasks 
        JOIN (
            SELECT taskId, MAX(`end`) as MaxEnd 
            FROM executions 
            GROUP BY taskId
        ) latestExecutions 
        ON tasks.taskId = latestExecutions.taskId 
        ORDER BY latestExecutions.MaxEnd DESC
        """
        )
        fun getAllOrderByLastDone(): Flow<List<Task>>

        @Query("SELECT * FROM tasks JOIN executions on tasks.taskId = executions.taskId ORDER BY executions.`end` desc")
        fun getAllOrderByHistory(): Flow<List<Task>>

        @Query("SELECT * FROM tasks WHERE NOT archived")//TODO: Implement query
        fun getAllOrderByUsuallyAtThisTime(): Flow<List<Task>>

        @Query("SELECT * FROM tasks WHERE taskId = :taskId")
        fun getByIdAsFlow(taskId: Long): Flow<Task>

        @Query("SELECT * FROM tasks WHERE taskId = :taskId")
        fun getById(taskId: Long): Task

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.childId WHERE TaskTaskCr.parentId = :parentId ORDER BY TaskTaskCR.position ASC")
        fun getSubTasks(parentId: Long): Flow<List<Task>>

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.childId WHERE TaskTaskCr.parentId = :parentId ORDER BY TaskTaskCR.position ASC")
        fun getSubTasksAsList(parentId: Long): List<Task>

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.parentId WHERE TaskTaskCr.childId = :childId")
        fun getParentTasks(childId: Long): Flow<List<Task>>

        @Transaction
        fun increaseTaskPosition(position: Long, parentId: Long) {
            if (position < taskLength(parentId)) {
                setTaskPosition(parentId, position, -position - 1)
                setTaskPosition(parentId, position + 1, position)
                setTaskPosition(parentId, -position - 1, position + 1)
            }
        }

        @Transaction
        fun decreaseTaskPosition(position: Long, parentId: Long) {
            if (position > 0) {
                setTaskPosition(parentId, position, -position - 1)
                setTaskPosition(parentId, position - 1, position)
                setTaskPosition(parentId, -position - 1, position - 1)
            }
        }

        @Transaction
        fun removeSubTask(parentTaskId: Long, position: Long) {
            deleteTaskTaskCR(parentTaskId, position)
            negatePositions(parentTaskId)
            increaseAndNegatePositionLowerThan(parentTaskId, -position)
        }

        @Query("UPDATE TaskTaskCR SET position = -position WHERE parentId = :parentTaskId")
        fun negatePositions(parentTaskId: Long)

        @Query("UPDATE TaskTaskCR SET position = position-1 WHERE parentId = :parentTaskId AND position>:position")
        fun decreasePositionGreaterThan(parentTaskId: Long, position: Long)

        @Query("UPDATE TaskTaskCR SET position =-position-1 WHERE parentId = :parentTaskId AND position<:position")
        fun increaseAndNegatePositionLowerThan(parentTaskId: Long, position: Long)

        @Query("DELETE FROM TaskTaskCR WHERE parentId = :parentTaskId AND position = :position")
        fun deleteTaskTaskCR(parentTaskId: Long, position: Long)

        @Query("UPDATE TaskTaskCR SET position = :newPosition WHERE parentId = :parentTaskId AND position = :position")
        fun setTaskPosition(parentTaskId: Long, position: Long, newPosition: Long)


        @Query("SELECT COUNT(*) FROM TaskTaskCR WHERE parentId = :taskId")
        fun taskLength(taskId: Long): Long

        //to get top and from from a percentile you must convert it to a fraction, so the numerator is top and from is the denominator. For example the 95% (0.95) percentile is 19/20 so top = 19 and from = 20
        @Query("""
            WITH LastExecutions AS (
                SELECT `end` - start AS duration
                FROM executions
                WHERE taskId = :taskId
                ORDER BY start DESC
                LIMIT :from
            )
            SELECT duration/60
            FROM LastExecutions
            ORDER BY duration ASC
            LIMIT 1 OFFSET :top-1
            """)
        fun maxTaskETA(taskId: Long, top: Int, from: Int): Flow<Long>

        @Query("""
            WITH ActivatorExecutions AS (
                SELECT `end` - start AS duration
                FROM executions
                WHERE activatorId = :activatorId
                ORDER BY start DESC
                LIMIT :from
            ),
            TaskExecutions AS (
                SELECT `end` - start AS duration
                FROM executions
                WHERE taskId = (SELECT taskToActivateId FROM activators WHERE activatorId = :activatorId)
                ORDER BY start DESC
                LIMIT :from
            )
            SELECT duration / 60
            FROM (
                SELECT * FROM ActivatorExecutions
                UNION ALL
                SELECT * FROM TaskExecutions
                WHERE (SELECT COUNT(*) FROM ActivatorExecutions) < :from
            )
            ORDER BY duration ASC
            LIMIT 1 OFFSET :top - 1
        """)
        fun maxActivatorETA(activatorId: Long, top: Int, from: Int): Flow<Long>
    }

    @Dao
    interface ActivatorDAO {
        @Insert
        fun insert(activator: Activator): Long

        @Update
        fun update(activator: Activator)

        @Upsert
        fun upsert(activator: Activator)

        @Delete
        fun delete(activator: Activator)

        @Query("SELECT * FROM activators")
        fun getAllActivators(): Flow<List<Activator>>

        @Query("SELECT * FROM activators WHERE NOT userCancelled AND COALESCE(endAfterDate > strftime('%s', 'now'),1) AND COALESCE(endAfterRepetitions > (SELECT COUNT(activatorId) FROM executions GROUP BY activatorId),1)")
        fun getActiveActivators(): Flow<List<Activator>>

        @Query("SELECT * FROM activators WHERE activatorId = :activatorId")
        fun getActivatorById(activatorId: Long): Activator

        @Query("SELECT * FROM activators WHERE activatorId = :activatorId")
        fun getActivatorByIdAsFlow(activatorId: Long): Flow<Activator>

        @Query("SELECT * FROM executions WHERE parentExecution IS NULL AND start = `end` ")
        fun getParentRunningExecutions(): Flow<List<Execution>>

        @Query(
            """
                SELECT activators.* 
                FROM activators 
                LEFT JOIN executions ON activators.activatorId = executions.activatorId 
                WHERE
                    activators.userCancelled = 0 AND
                    COALESCE(endAfterDate > strftime('%s', 'now'),1) AND
                    COALESCE(endAfterRepetitions > (SELECT COUNT(activatorId) FROM executions WHERE activatorId = activators.activatorId),1)
                GROUP BY activators.activatorId 
                HAVING 
                    (
                        repetitionUnit = 'DAYS' AND
                        (strftime('%s', 'now') - MAX(COALESCE(executions.`end`,activators.firstTimeDone ))) < activators.`end` * 86400 AND 
                        (strftime('%s', 'now') - MAX(COALESCE(executions.`end`,activators.firstTimeDone ))) > activators.start * 86400
                    ) OR
                    (
                        repetitionUnit = 'MONTHS' AND
                        strftime('%d', 'now') > strftime('%d', activators.start, 'unixepoch') AND
                        strftime('%d', 'now') < strftime('%d', activators.`end`, 'unixepoch') AND
                        strftime('%Y%m', 'now') NOT IN (SELECT strftime('%Y%m', `end`,'unixepoch') FROM executions WHERE activators.activatorId = executions.activatorId)
                    ) OR
                    (
                        repetitionUnit = 'YEARS' AND
                        (
                            (
                                dateTime('now') > dateTime(activators.start,'unixepoch',printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') AND
                                dateTime('now') < dateTime(activators.`end`,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
                            ) OR
                            (
                                dateTime('now') < dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years') 
                            )
                        ) AND
                        (
                            SELECT COUNT(*) FROM executions WHERE 
                                activators.activatorId = executions.activatorId AND
                                (
                                    (
                                        dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch',printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') AND
                                        dateTime(executions.`end`,'unixepoch') < dateTime(activators.`end`,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
                                    ) OR
                                    (
                                        dateTime(executions.`end`,'unixepoch') < dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years') 
                                    )
                                )
                        ) = 0
                    )
                ORDER BY MAX(COALESCE(executions.`end`,activators.firstTimeDone )) ASC
            """
        ) //TODO: implement querying exactDateRange pending tasks
        fun getPending(): Flow<List<Activator>>

        @Query(
            """
                SELECT activators.*
                FROM activators
                LEFT JOIN executions ON activators.activatorId = executions.activatorId
                WHERE 
                    activators.userCancelled = 0 AND
                    COALESCE(endAfterDate > strftime('%s', 'now'),1) AND
                    COALESCE(endAfterRepetitions > (SELECT COUNT(activatorId) FROM executions WHERE activatorId = activators.activatorId),1)
                GROUP BY activators.activatorId
                HAVING 
                    (
                        repetitionUnit = 'DAYS' AND 
                        (strftime('%s', 'now') - MAX(COALESCE(executions.`end`,activators.firstTimeDone ))) > activators.`end` * 86400
                    ) OR
                    (
                        repetitionUnit = 'MONTHS' AND
                        strftime('%d', 'now') > strftime('%d', activators.start, 'unixepoch') AND
                        strftime('%d', 'now') < strftime('%d', activators.`end`, 'unixepoch') AND
                        strftime('%Y%m', 'now') NOT IN (SELECT strftime('%Y%m', `end`,'unixepoch') FROM executions WHERE activators.activatorId = executions.activatorId)
                    ) OR
                    (
                        repetitionUnit = 'YEARS' AND 
                        (
                            dateTime('now') > dateTime(activators.`end`,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') AND
                            (
                                SELECT COUNT(*) FROM executions WHERE
                                    activators.activatorId = executions.activatorId AND
                                    dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years')
                            ) = 0
                        ) OR
                        (
                            dateTime('now') > dateTime(activators.`end`,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years') AND
                            dateTime('now') < dateTime(activators.start,'unixepoch', printf('%+d',abs(strftime('%Y','now') - strftime('%Y',activators.start,'unixepoch'))) || ' years') AND
                            (
                                SELECT COUNT(*) FROM executions WHERE
                                    activators.activatorId = executions.activatorId AND
                                    dateTime(executions.`end`,'unixepoch') > dateTime(activators.start,'unixepoch', printf('%+d',strftime('%Y','now', '-1 years') - strftime('%Y',activators.start,'unixepoch')) || ' years')
                            ) = 0
                        )
                    )
                ORDER BY MAX(COALESCE(executions.`end`,activators.firstTimeDone )) ASC
            """
        ) //TODO: implement querying exactDateRange overdue tasks
        fun getOverdue(): Flow<List<Activator>>
    }

    @Dao
    interface ExecutionDAO {
        @Insert
        fun insert(execution: Execution): Long

        @Update
        fun update(execution: Execution)

        @Query("SELECT * FROM executions WHERE executionId = :executionId")
        fun getById(executionId: Long): Execution

        @Query("UPDATE executions SET 'end' = :end, endReason = :endReason WHERE executionId = :executionId")
        fun update(
            executionId: Long, end: Int, endReason: EndReason
        )

        @Delete
        fun delete(execution: Execution)

        @Query("SELECT * FROM executions")
        fun getAllExecutions(): Flow<List<Execution>>

        @Query("SELECT * FROM executions WHERE executionId = :executionId")
        fun getExecutionById(executionId: Long): Execution

        @Query("SELECT * FROM executions WHERE parentExecution IS NULL AND `end` IS NULL")
        fun getRunningExecutions(): Flow<List<Execution>>

        @Query("SELECT * FROM executions WHERE executionId = :executionId")
        fun getExecutionWithTask(executionId: Long): ExecutionWithTask?
    }


    @Dao
    interface TaskWithTasksDAO {

        @Query(
            """
            WITH RECURSIVE idk(taskId) AS (
                SELECT :taskId
            UNION ALL
                SELECT TaskTaskCR.parentId FROM TaskTaskCR JOIN idk ON TaskTaskCR.childId = idk.taskId
            )
            SELECT * FROM tasks WHERE taskId NOT IN (SELECT taskId FROM idk)
        """
        )
        fun getTasksNotSubTasks(taskId: Long): Flow<List<Task>>

        @Transaction
        @Query("SELECT * FROM tasks")
        fun getTasksWithParentTasks(): Flow<List<TaskWithTasks>>

        @Query(
            """
    WITH RECURSIVE subtasks(taskId, path) AS (
        SELECT childId, ',' || childId || ',' FROM TaskTaskCR WHERE parentId = :taskId
        UNION ALL
        SELECT TaskTaskCR.childId, path || TaskTaskCR.childId || ',' FROM TaskTaskCR JOIN subtasks ON TaskTaskCR.parentId = subtasks.taskId WHERE path NOT LIKE '%' || TaskTaskCR.childId || '%'
    )
    SELECT * FROM tasks WHERE taskId IN (SELECT taskId FROM subtasks)
"""
        )
        fun getAllSubTasks(taskId: Long): List<Task>


        @Query(
            "INSERT INTO TaskTaskCR (parentId, childId, position) VALUES " + "(:parentTaskId, :taskId, COALESCE(((SELECT MAX(position) FROM TaskTaskCR WHERE parentId = :parentTaskId) + 1),0))"
        )
        fun addTaskAsLastSubTask(taskId: Long, parentTaskId: Long)
    }

}