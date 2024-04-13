package com.mrdarip.tasdks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val name: String,
    val comment: String?,
    val placeId: Long?
)

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true) val placeId: Long = 0,
    val name: String,
    val parentPlaceId: Long?
)

@Entity(tableName = "objects")
data class Object(
    @PrimaryKey(autoGenerate = true) val objectId: Long = 0,
    val name: String,
    val placeId: Long?
)

@Entity(tableName = "activators")
data class Activator(
    @PrimaryKey(autoGenerate = true) val activatorId: Long = 0,
    val comment: String?,
    val minRepSec: Int,
    val optRepSec: Int?,
    val maxRepSec: Int?,
    val endAfterDate: Date?,
    val endAfterRep: Int?,
    @ColumnInfo(defaultValue = "0") val userCancelled: Boolean
)

@Entity(tableName = "executions")
data class Execution(
    @PrimaryKey(autoGenerate = true) val executionId: Long = 0,
    val start: Date,
    val end: Date,
    val successfullyEnded: Boolean,
    val activatorId: Long,
    val resourceId: Long?
)

enum class ResourceType {
    MUSIC, AUDIOBOOK, PODCAST, VIDEO
}

@Entity(tableName = "resources")
data class Resource(
    @PrimaryKey(autoGenerate = true) val resourceId: Long = 0,
    val name: String,
    val resourceType: ResourceType
)


@Entity(primaryKeys = ["taskId", "objectId"])
data class TaskObjectCR(
    val taskId: Long,
    val objectId: Long
)

@Entity(primaryKeys = ["parentTaskId", "childTaskId"])
data class TaskTaskCR(
    val parentTaskId: Long,
    val childTaskId: Long
)

data class TasksByTask(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "parentTaskId",
        entityColumn = "childTaskId",
        associateBy = Junction(TaskTaskCR::class)
    )
    val songs: List<Task>
)

@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task : Task)

    @Update
    suspend fun update(item: Task)

    @Delete
    suspend fun delete(item: Task)

    @Query("SELECT * from tasks WHERE taskId = :id")
    fun getItem(id: Int): Flow<Task>

    @Query("SELECT * from tasks ORDER BY name ASC")
    fun getAllItems(): Flow<List<Task>>
}