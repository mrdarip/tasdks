package com.mrdarip.tasdks.data.entity

import androidx.room.Embedded
import androidx.room.Relation


data class ExecutionWithTask(
    @Embedded val user: Execution,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val task: Task
)