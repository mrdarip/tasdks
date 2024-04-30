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

        @Query("SELECT * FROM tasks")
        fun getAll(): Flow<List<Task>>

        @Query("SELECT * FROM tasks")//TODO: Implement query
        fun getAllOrderByLastDone(): Flow<List<Task>>

        @Query("SELECT * FROM tasks")//TODO: Implement query
        fun getAllOrderByUsuallyAtThisTime(): Flow<List<Task>>

        @Query("SELECT * FROM tasks WHERE taskId = :taskId")
        fun getById(taskId: Long): Flow<Task>

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.childTaskId WHERE TaskTaskCr.parentTaskId = :parentId ORDER BY TaskTaskCR.position ASC")
        fun getSubTasks(parentId: Long): Flow<List<Task>>

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.parentTaskId WHERE TaskTaskCr.childTaskId = :childId")
        fun getParentTasks(childId: Long): Flow<List<Task>>
        /*
        @Transaction
        @Query(
            """
            UPDATE
                TaskTaskCR
            SET
                position = CASE
                    WHEN childTaskId = :taskId THEN position + 1
                    ELSE position - 1
                END
            WHERE
                parentTaskId = :parentId
                AND childTaskId IN (
                    SELECT
                        childTaskId
                    FROM
                        TaskTaskCR
                    WHERE
                        parentTaskId = :parentId
                        AND (
                            position IN (
                                SELECT
                                    position
                                FROM
                                    TaskTaskCR
                                WHERE
                                    childTaskId = :taskId
                                    AND parentTaskId = :parentId
                            )
                            OR position IN (
                                SELECT
                                    position + 1
                                FROM
                                    TaskTaskCR
                                WHERE
                                    childTaskId = :taskId
                                    AND parentTaskId = :parentId
                            )
                        )
                )
            """
        )
        fun moveTaskUp(taskId: Long, parentId: Long)

        @Transaction
        @Query(
            """
            UPDATE
                TaskTaskCR
            SET
                position = CASE
                    WHEN childTaskId = :taskId THEN position - 1
                    ELSE position + 1
                END
            WHERE
                parentTaskId = :parentId
                AND childTaskId IN (
                    SELECT
                        childTaskId
                    FROM
                        TaskTaskCR
                    WHERE
                       parentTaskId = :parentId
                        AND (
                            position IN (
                                SELECT
                                    position
                                FROM
                                    TaskTaskCR
                                WHERE
                                    childTaskId = :taskId
                                    AND parentTaskId = :parentId
                           )
                            OR position IN (
                                SELECT
                                    position - 1
                                FROM
                                    TaskTaskCR
                                WHERE
                                    childTaskId = :taskId
                                    AND parentTaskId = :parentId
                            )
                        )
            )
            """
        )
        fun moveTaskDown(taskId: Long, parentId: Long)
*/

        @Transaction
        fun increaseTaskPosition(position: Long, parentId: Long) {
            setTaskPosition(parentId,position,-position-1)
            setTaskPosition(parentId,position+1,position)
            setTaskPosition(parentId,-position-1,position+1)
        }

        @Transaction
        fun decreaseTaskPosition(position: Long, parentId: Long) {
            setTaskPosition(parentId,position,-position-1)
            setTaskPosition(parentId,position-1,position)
            setTaskPosition(parentId,-position-1,position-1)
        }

        @Query("UPDATE TaskTaskCR SET position = :newPosition WHERE parentTaskId = :parentTaskId AND position = :position")
        fun setTaskPosition(parentTaskId: Long, position: Long, newPosition: Long)

        @Query("UPDATE TaskTaskCR SET position = :m * position + :n WHERE parentTaskId = :parentTaskId AND childTaskId = :taskId")
        fun operateTaskPositionmxn(parentTaskId: Long, taskId: Long, m: Int, n: Int)

        @Query("UPDATE TaskTaskCR SET position = :m * position + :n WHERE parentTaskId = :parentTaskId AND childTaskId = (SELECT childTaskId FROM TaskTaskCR WHERE parentTaskId = :parentTaskId AND position = (SELECT position FROM TaskTaskCR WHERE parentTaskId = :parentTaskId AND childTaskId = :taskId)+:relative) ")
        fun operateTaskPositionmxnRelative(
            taskId: Long,
            parentTaskId: Long,
            m: Int,
            n: Int,
            relative: Int
        )
    }

    @Dao
    interface PlaceDAO {
        @Insert
        fun insert(place: Place)

        @Update
        fun update(place: Place)

        @Delete
        fun delete(place: Place)

        @Query("SELECT * FROM places")
        fun getAllPlaces(): Flow<List<Place>>

        @Query("SELECT * FROM places WHERE placeId = :placeId")
        fun getPlaceById(placeId: Long): Flow<Place>
    }

    @Dao
    interface ObjectDAO {
        @Insert
        fun insert(obj: Object)

        @Update
        fun update(obj: Object)

        @Delete
        fun delete(obj: Object)

        @Query("SELECT * FROM objects")
        fun getAllObjects(): Flow<List<Object>>

        @Query("SELECT * FROM objects WHERE objectId = :objectId")
        fun getObjectById(objectId: Long): Object
    }

    @Dao
    interface ActivatorDAO {
        @Insert
        fun insert(activator: Activator)

        @Update
        fun update(activator: Activator)

        @Delete
        fun delete(activator: Activator)

        @Query("SELECT * FROM activators")
        fun getAllActivators(): Flow<List<Activator>>

        @Query("SELECT * FROM activators WHERE activatorId = :activatorId")
        fun getActivatorById(activatorId: Long): Activator
    }

    @Dao
    interface ExecutionDAO {
        @Insert
        fun insert(execution: Execution)

        @Update
        fun update(execution: Execution)

        @Delete
        fun delete(execution: Execution)

        @Query("SELECT * FROM executions")
        fun getAllExecutions(): Flow<List<Execution>>

        @Query("SELECT * FROM executions WHERE executionId = :executionId")
        fun getExecutionById(executionId: Long): Execution
    }

    @Dao
    interface ResourceDAO {
        @Insert
        fun insert(resource: Resource)

        @Update
        fun update(resource: Resource)

        @Delete
        fun delete(resource: Resource)

        @Query("SELECT * FROM resources")
        fun getAllResources(): Flow<List<Resource>>

        @Query("SELECT * FROM resources WHERE resourceId = :resourceId")
        fun getResourceById(resourceId: Long): Resource
    }

    @Dao
    interface TaskWithTasksDAO {
        @Transaction
        @Query("SELECT * FROM tasks")
        fun getTasksWithParentTasks(): Flow<List<TaskWithTasks>>

        @Query("INSERT INTO TaskTaskCR (parentTaskId, childTaskId, position) VALUES (:parentTaskId, :taskId, (SELECT MAX(position) FROM TaskTaskCR WHERE parentTaskId = :parentTaskId) + 1)")
        fun addTaskAsLastSubTask(taskId: Long, parentTaskId: Long)
    }

    @Dao
    interface TaskWithObjectsDAO {
        @Transaction
        @Query("SELECT * FROM tasks")
        fun getTasksWithObjects(): Flow<List<TaskWithObjects>>
    }
}