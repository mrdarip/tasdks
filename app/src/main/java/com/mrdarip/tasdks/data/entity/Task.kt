package com.mrdarip.tasdks.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @param name the name of the task
 * @param comment the comment of the task
 * @param iconEmoji the emoji of the task
 * @param archived if the task is archived
 * @param createdTime the time the task was created in seconds since epoch (unix time)
 * @param waitTime the time to wait before the task can be completed, in seconds
 * @param allowParallelTasks if the task allows parallel tasks
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    var name: String = "",
    val comment: String? = null,
    val iconEmoji: String? = null,
    val archived: Boolean = false,
    val isFavourite: Boolean = false,
    val createdTime: Double = System.currentTimeMillis() / 1000.0,
    val waitTime: Int = 0, //In seconds //for tasks that need waiting until you can do the next task
    val allowParallelTasks: Boolean = false, //for tasks that can be done at the same time as other tasks
)

