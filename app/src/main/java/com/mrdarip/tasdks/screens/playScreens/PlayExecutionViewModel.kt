package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.EndReason
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.ExecutionWithTask
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayExecutionViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(PlayExecutionState())
        private set

    fun undoExecution() {
        TODO("Not yet implemented")
    }

    fun completeExecution() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val actualExecution = state.actualExecution.execution
            val newExecution = actualExecution.copy(
                end = unixEpochTime(),
                endReason = EndReason.SUCCESS
            )

            repository.upsertExecution(newExecution)
        }
    }

    fun setTopExecution(execution: Execution) { //fixme: this code is horrible
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.w("PlayExecutionViewModel", "setTopExecution($execution)")


            lateinit var topExecution: ExecutionWithTask
            lateinit var actualExecution: ExecutionWithTask
            if (execution.executionId != 0L) {
                topExecution = repository.getExecutionWithTaskByExeId(execution.executionId)
                    ?: ExecutionWithTask(Execution(taskId = -1), Task())

                actualExecution =
                    repository.getRunningExecutionChildOf(topExecution.execution.executionId)
            } else {
                if (execution.taskId != 0L) {
                    topExecution =
                        ExecutionWithTask(execution, repository.getTaskById(execution.taskId))
                } else {
                    if (execution.activatorId != null) {
                        topExecution = ExecutionWithTask(
                            execution,
                            repository.getTaskByActivatorId(execution.activatorId)
                        )
                    } else {
                        throw IllegalArgumentException("Execution must have a taskId or activatorId")
                    }
                }
                actualExecution = topExecution
            }


            Log.w(
                "PlayExecutionViewModel",
                "setTopExecution($execution) topExecution=$topExecution"
            )


            state = state.copy(topExecution = topExecution, actualExecution = actualExecution)
        }
    }
}

data class PlayExecutionState(
    val topExecution: ExecutionWithTask? = null,
    val actualExecution: ExecutionWithTask = ExecutionWithTask(Execution(taskId = -1), Task()),
    val nextTask: Task? = null
)

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}