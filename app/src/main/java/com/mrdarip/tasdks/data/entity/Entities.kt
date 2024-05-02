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
    @PrimaryKey(autoGenerate = true) val taskId: Long? = null,
    var name: String,
    val comment: String?, //iconEmoji can be null so later on you can know what tasks weren't given an emoji
    val iconEmoji: String?,
    val placeId: Long?
)

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true) val placeId: Long? = null,
    val name: String,
    val parentPlaceId: Long?
)

@Entity(tableName = "objects")
data class Object(
    @PrimaryKey(autoGenerate = true) val objectId: Long? = null,
    val name: String,
    val placeId: Long?
)

enum class RepetitionType {
    DATE, MINUTES,HOURS, DAYS, WEEKS, MONTHS, YEARS//TODO Check how to manage time repetition vs date repetition ( a date is millis since epoch...)
}

data class RepetitionRange(
    val minRepSec: Int,
    val optRepSec: Int?,
    val maxRepSec: Int?,
    val repetitionType: RepetitionType
)
@Entity(tableName = "activators")
data class Activator(
    @PrimaryKey(autoGenerate = true) val activatorId: Long? = null,
    val comment: String?,
    @Embedded val repetitionRange: RepetitionRange,
    val endAfterDate: Date?,
    val endAfterRep: Int?,
    @ColumnInfo(defaultValue = "0") val userCancelled: Boolean,
    val taskToActivateId: Long
)

@Entity(tableName = "executions")
data class Execution(
    @PrimaryKey(autoGenerate = true) val executionId: Long? = null,
    val start: Date?,
    val end: Date?,
    val successfullyEnded: Boolean,
    val activatorId: Long,
    val resourceId: Long?,
    val parentExecution: Long?,
    val taskId: Long
)

enum class ResourceType {
    MUSIC, AUDIOBOOK, PODCAST, VIDEO
}

@Entity(tableName = "resources")
data class Resource(
    @PrimaryKey(autoGenerate = true) val resourceId: Long? = null,
    val name: String,
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