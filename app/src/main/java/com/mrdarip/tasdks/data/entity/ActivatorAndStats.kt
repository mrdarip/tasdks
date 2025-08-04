package com.mrdarip.tasdks.data.entity

import androidx.room.Embedded

data class ActivatorAndStats(
    @Embedded val activator: Activator,
    @Embedded(prefix = "task_") val task: Task,
    val estimatedTimeMinutes: Long
)
