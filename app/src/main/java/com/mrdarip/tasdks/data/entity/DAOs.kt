package com.mrdarip.tasdks.data.entity


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

class DAOs {
    @Dao
    interface TaskDAO {
        @Insert
        suspend fun insert(task: Task)

        @Update
        fun update(task: Task)

        @Delete
        fun delete(task: Task)

        @Query("SELECT * FROM tasks")
        fun getAll(): Flow<List<Task>>

        @Query("SELECT * FROM tasks")//TODO: Implement query
        fun getAllOrderByLastDone(): Flow<List<Task>>

        @Query("SELECT * FROM tasks")//TODO: Implement query
        fun getAllOrderByUsuallyAtThisTime(): Flow<List<Task>>

        @Query("SELECT * FROM tasks WHERE taskId = :taskId")
        fun getById(taskId: Long): Task
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
    }

    @Dao
    interface TaskWithObjectsDAO {
        @Transaction
        @Query("SELECT * FROM tasks")
        fun getTasksWithObjects(): Flow<List<TaskWithObjects>>
    }
}