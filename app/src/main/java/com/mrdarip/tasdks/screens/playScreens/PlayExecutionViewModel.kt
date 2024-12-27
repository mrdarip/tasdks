package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.ActivatorWithTask
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.ExecutionWithTask
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.data.entity.idRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayExecutionViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(PlayExecutionState())
        private set

    var isStarted by mutableStateOf(false)
        private set


    fun undoExecution() {
        TODO("Not yet implemented")
    }

    fun completeExecution() {
        TODO()
    }

    fun setTopExecution(execution: Execution) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val topExecution: ExecutionWithTask
            val actualExecution: ExecutionWithTask

            if (execution.executionId != 0L) {
                Log.i("PlayExecutionViewModel", "ExecutionId: ${execution.executionId}")
                topExecution = repository.getExecutionWithTaskByExeId(execution.executionId)
                actualExecution = repository.getRunningExecutionChildOf(execution.executionId)
            } else {
                if (execution.activatorId == null) {
                    Log.i("PlayExecutionViewModel", "TaskId: ${execution.taskId}")
                    val task = repository.getTaskById(execution.taskId)
                    topExecution = ExecutionWithTask(Execution.of(task), task)
                } else {
                    Log.i("PlayExecutionViewModel", "ActivatorId: ${execution.activatorId}")
                    val activatorWithTask: ActivatorWithTask =
                        repository.getActivatorWithTaskByActivatorId(execution.activatorId)
                    topExecution = ExecutionWithTask(
                        Execution.of(activatorWithTask.activator),
                        activatorWithTask.task
                    )
                }
                actualExecution = topExecution
            }

            state = state.copy(topExecution = topExecution, actualExecution = actualExecution)
            evaluateIsStarted()
        }
    }

    fun startActualExecution() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val nextActualExecution = repository.startExecution(state.actualExecution)

            state = state.copy(actualExecution = nextActualExecution)
            isStarted = true
        }
    }

    private fun evaluateIsStarted() {
        isStarted = state.actualExecution.execution.isStarted()
    }
}

data class PlayExecutionState(
    val topExecution: ExecutionWithTask? = null,
    val actualExecution: ExecutionWithTask = ExecutionWithTask(
        Execution(
            taskId = -1,
            routeIds = idRoute(emptyList()),
            childNumber = 0
        ), Task()
    ),
    val nextTask: Task? = null
)

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}