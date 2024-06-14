package com.mrdarip.tasdks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    var name: String = "",
    val comment: String? = null, //iconEmoji can be null so later on you can know what tasks weren't given an emoji
    val iconEmoji: String? = null,
    val placeId: Long? = null,
    val archived: Boolean = false,
    val createdTime: Double = System.currentTimeMillis() / 1000.0,

    val isPlaylist: Boolean = false, //for tasks that allow skipping its direct subtasks
    val waitTime: Int = 0, //In seconds //for tasks that need waiting until you can do the next task
    val allowParallelTasks: Boolean = false, //for tasks that can be done at the same time as other tasks
)

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true) val placeId: Long = 0,
    val name: String = "Place " + Date(System.currentTimeMillis()).toString(),
    val parentPlaceId: Long? = null
)

@Entity(tableName = "objects")
data class Object(
    @PrimaryKey(autoGenerate = true) val objectId: Long = 0,
    val name: String = "Object " + Date(System.currentTimeMillis()).toString(),
    val placeId: Long? = null
)

enum class RepetitionUnit {
    MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS//TODO Check how to manage time repetition vs date repetition ( a date is millis since epoch...)
}

data class RepetitionRange(
    val exactDateRange: Boolean = false,
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
    val endsAfterDate: Boolean? = null, //null as it doesn't end by default //TODO: Use it
    val endDate: Int? = null, //In seconds since epoch
    val endRep: Int? = 1, //TODO: Restrict EndAfterRep so it can't be 0, should be null instead
    @ColumnInfo(defaultValue = "0") val userCancelled: Boolean = false,
    val taskToActivateId: Long,
    val createdTime: Double = System.currentTimeMillis() / 1000.0
)

@Entity(tableName = "executions")
data class Execution(
    @PrimaryKey(autoGenerate = true) val executionId: Long = 0,
    val start: Int? = null, //In seconds since epoch
    val end: Int? = null,//In seconds since epoch
    val successfullyEnded: Boolean = false,
    val activatorId: Long, //activator that triggered the the root of the execution tree
    val resourceId: Long? = null,
    val parentExecution: Long?,
    val taskId: Long
)

enum class ResourceType {
    MUSIC, AUDIOBOOK, PODCAST, VIDEO
}

@Entity(tableName = "resources")
data class Resource(
    @PrimaryKey(autoGenerate = true) val resourceId: Long = 0,
    val name: String = "Resource " + Date(System.currentTimeMillis()).toString(),
    val resourceType: ResourceType
)


@Entity(primaryKeys = ["taskId", "objectId"])
data class TaskObjectCR(
    val taskId: Long,
    val objectId: Long
)

@Entity(primaryKeys = ["parentTaskId", "position"])
data class TaskTaskCR(
    val parentTaskId: Long,
    val childTaskId: Long,
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
            parentColumn = "parentTaskId",
            entityColumn = "childTaskId"
        )
    )
    val tasks: List<Task>
)

data class TaskWithObjects(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "objectId",
        associateBy = Junction(TaskObjectCR::class)
    )
    val objects: List<Object>
)

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}