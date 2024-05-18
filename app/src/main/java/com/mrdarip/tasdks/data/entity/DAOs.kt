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
        fun getByIdAsFlow(taskId: Long): Flow<Task>

        @Query("SELECT * FROM tasks WHERE taskId = :taskId")
        fun getById(taskId: Long): Task

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.childTaskId WHERE TaskTaskCr.parentTaskId = :parentId ORDER BY TaskTaskCR.position ASC")
        fun getSubTasks(parentId: Long): Flow<List<Task>>

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.childTaskId WHERE TaskTaskCr.parentTaskId = :parentId ORDER BY TaskTaskCR.position ASC")
        fun getSubTasksAsList(parentId: Long): List<Task>

        @Query("SELECT * FROM tasks INNER JOIN TaskTaskCr ON tasks.taskId = TaskTaskCR.parentTaskId WHERE TaskTaskCr.childTaskId = :childId")
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
        fun removeSubTask(parentTaskId: Long,position: Long){
            deleteTaskTaskCR(parentTaskId, position)
            decreasePositionGreaterThan(parentTaskId, position)
        }
        @Query("UPDATE TaskTaskCR SET position = position-1 WHERE parentTaskId = :parentTaskId AND position>:position")
        fun decreasePositionGreaterThan(parentTaskId: Long,position: Long)

        @Query("DELETE FROM TaskTaskCR WHERE parentTaskId = :parentTaskId AND position = :position")
        fun deleteTaskTaskCR(parentTaskId: Long,position: Long)
        @Query("UPDATE TaskTaskCR SET position = :newPosition WHERE parentTaskId = :parentTaskId AND position = :position")
        fun setTaskPosition(parentTaskId: Long, position: Long, newPosition: Long)



        @Query("SELECT COUNT(*) FROM TaskTaskCR WHERE parentTaskId = :taskId")
        fun taskLength(taskId: Long): Long
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
        fun insert(activator: Activator): Long

        @Update
        fun update(activator: Activator)

        @Upsert
        fun upsert(activator: Activator)

        @Delete
        fun delete(activator: Activator)

        @Query("SELECT * FROM activators")
        fun getAllActivators(): Flow<List<Activator>>

        @Query("SELECT * FROM activators WHERE NOT userCancelled AND COALESCE(endAfterDate > strftime('%s', 'now'),1) AND endAfterRep > (SELECT COUNT(*) FROM executions WHERE activatorId = activatorId)")
        fun getActiveActivators(): Flow<List<Activator>>

        @Query("SELECT * FROM activators WHERE activatorId = :activatorId")
        fun getActivatorById(activatorId: Long): Activator

        @Query("SELECT * FROM activators WHERE activatorId = :activatorId")
        fun getActivatorByIdAsFlow(activatorId: Long): Flow<Activator>
    }

    @Dao
    interface ExecutionDAO {
        @Insert
        fun insert(execution: Execution): Long

        @Update
        fun update(execution: Execution)

        @Query("SELECT * FROM executions WHERE executionId = :executionId")
        fun getById(executionId: Long): Execution

        @Query("UPDATE executions SET 'end' = :end, successfullyEnded = :successfullyEnded WHERE executionId = :executionId")
        fun update(
            executionId: Long,
            end: Int,
            successfullyEnded: Boolean
        )

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

        @Query("""
            WITH RECURSIVE idk(taskId) AS (
                SELECT :taskId
            UNION ALL
                SELECT TaskTaskCR.parentTaskId FROM TaskTaskCR JOIN idk ON TaskTaskCR.childTaskId = idk.taskId
            )
            SELECT * FROM tasks WHERE taskId NOT IN (SELECT taskId FROM idk)
        """)
        fun getTasksNotSubTasks(taskId: Long): Flow<List<Task>>

        @Transaction
        @Query("SELECT * FROM tasks")
        fun getTasksWithParentTasks(): Flow<List<TaskWithTasks>>

        @Query("""
    WITH RECURSIVE subtasks(taskId, path) AS (
        SELECT childTaskId, ',' || childTaskId || ',' FROM TaskTaskCR WHERE parentTaskId = :taskId
        UNION ALL
        SELECT TaskTaskCR.childTaskId, path || TaskTaskCR.childTaskId || ',' FROM TaskTaskCR JOIN subtasks ON TaskTaskCR.parentTaskId = subtasks.taskId WHERE path NOT LIKE '%' || TaskTaskCR.childTaskId || '%'
    )
    SELECT * FROM tasks WHERE taskId IN (SELECT taskId FROM subtasks)
""")
        fun getAllSubTasks(taskId: Long): List<Task>


        @Query(
            "INSERT INTO TaskTaskCR (parentTaskId, childTaskId, position) VALUES " +
                    "(:parentTaskId, :taskId, COALESCE(((SELECT MAX(position) FROM TaskTaskCR WHERE parentTaskId = :parentTaskId) + 1),0))"
        )
        fun addTaskAsLastSubTask(taskId: Long, parentTaskId: Long)
    }

    @Dao
    interface TaskWithObjectsDAO {
        @Transaction
        @Query("SELECT * FROM tasks")
        fun getTasksWithObjects(): Flow<List<TaskWithObjects>>
    }
}