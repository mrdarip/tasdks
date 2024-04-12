package com.mrdarip.tasdks.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val name: String
){}

@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val name: String,
    val placeId: Long
){}

@Entity(tableName = "Places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val placeId: Long = 0,
    val name: String,
    val parentPlaceId: Long
){}

@Entity(tableName = "Objects")
data class Object(
    @PrimaryKey(autoGenerate = true)
    val objectId: Long = 0,
    val name: String,
    val placeId: Long
){}

@Entity(tableName = "Activators")
data class Activator(
    @PrimaryKey(autoGenerate = true)
    val activatorId: Long = 0,
    val comment: String,
    val minRepSec: UInt,
    val optRepSec: UInt,
    val maxRepSec: UInt,
    val endAfterDate: Date,
    val endAfterRep: UInt
){}

@Entity(tableName = "Executions")
data class Execution(
    @PrimaryKey(autoGenerate = true)
    val executionId: Long = 0,
    val startDate: Date,
    val endDate: Date,
    val activatorId: Long,
    val resourceId: Long
){}

enum class ResourceType {
    MUSIC,AUDIOBOOK,PODCAST
}

@Entity(tableName = "Resources")
data class Resource(
    @PrimaryKey(autoGenerate = true)
    val resourceId: Long = 0,
    val name: String,
    val resourceType: ResourceType
){}




@Entity()
data class PlaylistTaskCR(
    val playlistId: Long,
    val taskId: Long
){}

@Entity()
data class TaskObjectCR(
    val playlistId: Long,
    val taskId: Long
){}

@Entity()
data class TaskParentCR(
    val playlistId: Long,
    val taskId: Long
){}

