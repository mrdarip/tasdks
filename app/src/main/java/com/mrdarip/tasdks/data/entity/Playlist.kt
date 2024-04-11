package com.mrdarip.tasdks.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val name: String
){}

@Entity()
data class PlaylistTaskCR(
    val playlistId: Long,
    val taskId: Long
){}

