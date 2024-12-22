package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayExecutionViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(PlayActivatorState())
        private set


    lateinit var currentExecution: Execution


    fun getCurrentExecutionParent(): Execution? {
        return repository.getParentExecution(currentExecution)
    }

    fun getCurrentExecutionBrothers(): List<Task> {
        val parentSubTasks = repository.getSubTasksOfTaskAsList(currentExecution.taskId)
        val currentExecutionIndex: Int =
            parentSubTasks.indexOfFirst { it.taskId == currentExecution.taskId }
        return parentSubTasks.subList(
            (currentExecutionIndex - 1).coerceAtLeast(0),
            (currentExecutionIndex + 2).coerceAtMost(parentSubTasks.size)
        )
    }

    fun isRunning(): Boolean {
        return false
    }

    fun setExecution(executionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            currentExecution = repository.getExecutionById(executionId)
        }
    }
}

data class PlayActivatorState(
    val tasks: List<Task> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    //TODO: Add other entities video 6/7
)

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}