package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.EndReason
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.data.entity.TaskWithActivator
import com.mrdarip.tasdks.data.entity.idRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlayExecutionViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(PlayExecutionState())
        private set

    var isStarted by mutableStateOf(false)
        private set

    private val _completedExecutions = MutableLiveData<Boolean>()
    val completedExecutions: LiveData<Boolean> get() = _completedExecutions

    private val _actualParents = MutableLiveData<List<Task>>()
    val actualParents: LiveData<List<Task>> get() = _actualParents

    init {
        getActualExecutionParentTasks()
    }

    private fun getActualExecutionParentTasks() {
        viewModelScope.launch {
            repository.getParentTasksOfExecutionsFlow(state.actualExecution).collectLatest {
                _actualParents.postValue(it)
            }
        }
    }


    fun undoExecution() {
        TODO("Not yet implemented")
    }

    fun setTopExecution(execution: Execution) {
        if (state.topExecution == null) {
            viewModelScope.launch(context = Dispatchers.IO) {
                val topExecution: ExecutionWithTaskAndActivator
                val actualExecution: ExecutionWithTaskAndActivator

                if (execution.executionId != 0L) {
                    Log.i(
                        "PlayExecutionViewModel",
                        "Executing with ExecutionId: ${execution.executionId}"
                    )

                    topExecution =
                        repository.getExecutionWithTaskAndActivatorByExeId(execution.executionId)

                    val actualExecutionWithTask =
                        repository.getRunningExecutionChildOf(execution.executionId)
                    actualExecution = ExecutionWithTaskAndActivator(
                        actualExecutionWithTask.execution,
                        actualExecutionWithTask.task,
                        topExecution.activator
                    )

                } else {
                    if (execution.activatorId != null) {
                        Log.i(
                            "PlayExecutionViewModel",
                            "Executing with ActivatorId: ${execution.activatorId}"
                        )

                        val activatorWithTask =
                            repository.getActivatorWithTaskByActivatorId(execution.activatorId)
                        val task = activatorWithTask.task
                        val activator = activatorWithTask.activator

                        val newExecution = Execution.of(TaskWithActivator(task, activator))

                        topExecution = ExecutionWithTaskAndActivator(
                            newExecution,
                            task,
                            activator
                        )
                    } else {
                        Log.i(
                            "PlayExecutionViewModel",
                            "Executing with TaskId: ${execution.taskId}"
                        )

                        val task = repository.getTaskById(execution.taskId)
                        val activator = null
                        val newExecution = Execution.of(TaskWithActivator(task, activator))

                        topExecution = ExecutionWithTaskAndActivator(
                            newExecution,
                            task,
                            activator
                        )
                    }
                    actualExecution = topExecution
                }

                state = state.copy(topExecution = topExecution, actualExecution = actualExecution)
                evaluateIsStarted()
            }
        }
    }

    fun startActualExecution() {
        viewModelScope.launch(context = Dispatchers.IO) {
            val nextActualExecution = repository.startExecution(state.actualExecution)

            state = state.copy(actualExecution = nextActualExecution)
            isStarted = true

            getActualExecutionParentTasks()
        }
    }


    fun completeExecution(reason: EndReason) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val nextActualExecution = repository.completeExecution(
                state.actualExecution, reason
            )

            if (nextActualExecution != null) {
                state = state.copy(actualExecution = nextActualExecution)
            } else {
                _completedExecutions.postValue(true)
            }

            getActualExecutionParentTasks()
        }
    }

    private fun evaluateIsStarted() {
        isStarted = state.actualExecution.execution.isStarted()
    }
}

data class PlayExecutionState(
    val topExecution: ExecutionWithTaskAndActivator? = null,
    val actualExecution: ExecutionWithTaskAndActivator = ExecutionWithTaskAndActivator(
        Execution(
            taskId = -1,
            tasksRoute = idRoute(emptyList()),
            executionRoute = idRoute(emptyList()),
            childNumber = 0
        ), Task(), null
    ),
    val nextTask: Task? = null
)

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}