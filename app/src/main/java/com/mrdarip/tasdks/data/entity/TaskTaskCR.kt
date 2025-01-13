package com.mrdarip.tasdks.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation


@Entity(primaryKeys = ["parentId", "position"])
data class TaskTaskCR(
    val parentId: Long,
    val childId: Long,
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
            parentColumn = "parentId",
            entityColumn = "childId"
        )
    )
    val tasks: List<Task>
)