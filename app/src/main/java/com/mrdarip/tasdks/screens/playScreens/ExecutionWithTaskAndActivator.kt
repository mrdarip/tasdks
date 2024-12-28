package com.mrdarip.tasdks.screens.playScreens

import androidx.room.Embedded
import androidx.room.Relation
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Task


data class ExecutionWithTaskAndActivator(
    @Embedded val execution: Execution,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val task: Task,
    @Relation(
        parentColumn = "activatorId",
        entityColumn = "activatorId"
    )
    val activator: Activator?
)