package com.mrdarip.tasdks.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithActivator(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskToActivateId"
    )
    val activator: Activator?
)