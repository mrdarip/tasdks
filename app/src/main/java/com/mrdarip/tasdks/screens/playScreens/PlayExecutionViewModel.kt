package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayExecutionViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(PlayActivatorState())
        private set


    val currentExecution = MutableStateFlow<Execution?>(null)

    fun isRunning(): Boolean {
        return true
    }

    fun setContextExecution(executionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("PlayExecutionViewModel", "setExecution: $executionId")
            currentExecution.value = repository.getExecutionById(executionId)
        }
    }

    fun getTopExecutionTask(): Task {
        return Task(
            name = "Task 1",
            iconEmoji = "🎉"
        )
    }

    fun getActualExecutionTask(): Task {
        return Task(
            name = "Task 2",
            iconEmoji = "🎉"
        )
    }

    fun getNextTask(): Task {
        return Task(
            name = "Task 3",
            iconEmoji = "🎉"
        )
    }

    fun undoExecution() {
        TODO("Not yet implemented")
    }

    fun completeExecution() {
        TODO("Not yet implemented")
    }
}

data class PlayActivatorState(
    val tasks: List<Task> = emptyList()
)

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}