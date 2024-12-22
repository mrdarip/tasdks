package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
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
        TODO("Not yet implemented")
    }

    fun setTopExecution(executionId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val execution = repository.getExecutionWithTask(executionId)
            state = state.copy(topExecution = execution)
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