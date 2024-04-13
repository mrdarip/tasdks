package com.mrdarip.tasdks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val name: String,
    val comment: String?,
    val placeId: Long?
) {}

@Entity(tableName = "Places")
data class Place(
    @PrimaryKey(autoGenerate = true) val placeId: Long = 0,
    val name: String,
    val parentPlaceId: Long?
) {}

@Entity(tableName = "Objects")
data class Object(
    @PrimaryKey(autoGenerate = true) val objectId: Long = 0,
    val name: String,
    val placeId: Long?
) {}

@Entity(tableName = "Activators")
data class Activator(
    @PrimaryKey(autoGenerate = true) val activatorId: Long = 0,
    val comment: String?,
    val minRepSec: UInt,
    val optRepSec: UInt?,
    val maxRepSec: UInt?,
    val endAfterDate: Date?,
    val endAfterRep: UInt?,
    @ColumnInfo(defaultValue = "0") val userCancelled: Boolean
) {}

@Entity(tableName = "Executions")
data class Execution(
    @PrimaryKey(autoGenerate = true) val executionId: Long = 0,
    val start: Date,
    val end: Date,
    val successfullyEnded: Boolean,
    val activatorId: Long,
    val resourceId: Long?
) {}

enum class ResourceType {
    MUSIC, AUDIOBOOK, PODCAST, VIDEO
}

@Entity(tableName = "Resources")
data class Resource(
    @PrimaryKey(autoGenerate = true) val resourceId: Long = 0,
    val name: String,
    val resourceType: ResourceType
) {}


@Entity(primaryKeys = ["taskId", "objectId"])
data class TaskObjectCR(
    val taskId: Long,
    val objectId: Long
) {}

@Entity(primaryKeys = ["parentTaskId", "childTaskId"])
data class TaskParentCR(
    val parentTaskId: Long,
    val childTaskId: Long
) {}

