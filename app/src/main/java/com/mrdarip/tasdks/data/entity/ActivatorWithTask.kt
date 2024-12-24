package com.mrdarip.tasdks.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
data class ActivatorWithTask(
    @Embedded val activator: Activator,
    @Relation(
        parentColumn = "taskToActivateId",
        entityColumn = "taskId"
    )
    val task: Task
)
