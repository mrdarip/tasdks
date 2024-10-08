package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayActivatorViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    /*
    private val _startedTasksName = MutableStateFlow<MutableList<String>>(mutableListOf())
    val startedTasksName = _startedTasksName.asStateFlow()
    fun addStartedTaskName(taskName: String) {
        _startedTasksName.value.add(taskName)
    }

    fun removeLastStartedTaskName() {
        _startedTasksName.value.removeLast()
    }

     */

    private val _topActivatorId = MutableStateFlow<Long>(-1)
    val topActivatorId = _topActivatorId.asStateFlow()
    fun setTopActivatorId(activatorId: Long) {
        _topActivatorId.value = activatorId
    }

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList = _taskList.asStateFlow()
    fun addToTasksList(task: Task) {
        _taskList.value += task
    }

    fun removeFromTaskList() {
        _taskList.value = _taskList.value.dropLast(1)
    }
    private val _currentExecutionId = MutableStateFlow<Long>(-1)
    val currentExecutionId = _currentExecutionId.asStateFlow()
    fun setCurrentExecutionId(executionId: Long) {
        _currentExecutionId.value = executionId
    }

    private val _runningExecutionsIds = MutableStateFlow<List<Long>>(emptyList())
    val runningExecutionsIds = _runningExecutionsIds.asStateFlow()
    fun addRunningExecutionsId(executionId: Long) {
        _runningExecutionsIds.value += executionId
    }

    fun removeLastRunningExecutionId() {
        _runningExecutionsIds.value = _runningExecutionsIds.value.dropLast(1)
    }

    private val _positions = MutableStateFlow<List<Int>>(emptyList())
    val positions = _positions.asStateFlow()
    fun appendPosition(position: Int) {
        _positions.value += position
    }

    fun removeLastPosition() {
        _positions.value = _positions.value.dropLast(1)
    }

    fun addOneToLastPosition() {
        val lastPosition = _positions.value.last()
        _positions.value = _positions.value.dropLast(1) + (lastPosition + 1)
    }

    var state by mutableStateOf(PlayActivatorState())
        private set

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun upsertTask(task: Task) {
        viewModelScope.launch {
            repository.upsertTask(task)
        }
    }

    fun insertTask(task: Task): Long {
        var newTaskId = 0L
        viewModelScope.launch {
            newTaskId = repository.insertTask(task)
        }
        return newTaskId
    }

    fun getTaskById(taskId: Long): Task {
        return repository.getTaskById(taskId)
    }

    fun getTaskByIdAsFlow(taskId: Long): Flow<Task> {
        return repository.getTaskByIdAsFlow(taskId)
    }

    fun getSubTasksOfTask(taskId: Long): Flow<List<Task>> {
        return repository.getSubTasksOfTask(taskId)
    }

    fun getParentTasksOfTask(taskId: Long): Flow<List<Task>> {
        return repository.getParentTasksOfTask(taskId)
    }

    fun insertExecution(execution: Execution): Long {
        return repository.insertExecution(execution)
    }

    fun updateExecution(execution: Execution) {
        repository.updateExecution(execution)
    }

    fun updateExecution(
        executionId: Long,
        end: Int,
        successfullyEnded: Boolean
    ) {
        repository.updateExecution(executionId, end, successfullyEnded)
    }

    fun getSubTasksOfTaskAsList(taskId: Long): List<Task> {
        return repository.getSubTasksOfTaskAsList(taskId)
    }


    fun getActivatorById(activatorId: Long): Activator {
        return repository.getActivatorById(activatorId)
    }

    fun getActivatorByIdAsFlow(activatorId: Long): Flow<Activator> {
        return repository.getActivatorByIdAsFlow(activatorId)
    }

    fun getExecutionById(executionId: Long): Execution {
        return repository.getExecutionById(executionId)
    }


    fun start(newTask: Task, parentExecution: Execution?) {
        addToTasksList(newTask)
        Log.i("PlayActivatorScreen", "Tarea actual: ${newTask.name}")
        val currentExecutionId = insertExecution(
            Execution(
                start = unixEpochTime(),
                end = unixEpochTime(),
                successfullyEnded = false,
                activatorId = topActivatorId.value, //TODO: Check is read only
                resourceId = null, //by now we don't implement resources
                parentExecution = parentExecution?.executionId,
                taskId = newTask.taskId
            )
        )
        setCurrentExecutionId(currentExecutionId)

        val currentTaskSubtasks = getSubTasksOfTaskAsList(newTask.taskId)
        if (currentTaskSubtasks.isNotEmpty()) {
            Log.i("PlayActivatorScreen", "Tenía hijos!")
            appendPosition(0)
            start(currentTaskSubtasks[0], getExecutionById(currentExecutionId))
        }
    }


    fun checkExecution(execution: Execution, onEnd: () -> Unit) {
        Log.i("PlayActivatorScreen", "Check ${execution.taskId} with id ${execution.executionId}")
        Log.i("PlayActivatorScreen", "Entramos con la lista en ${positions.value}")


        removeFromTaskList()

        updateExecution(
            executionId = execution.executionId,
            end = unixEpochTime(),
            successfullyEnded = true
        )

        val hasBrothers = execution.parentExecution != null && getSubTasksOfTaskAsList(
            getExecutionById(
                execution.parentExecution
            ).taskId
        ).size > 1

        val hasNextBrother =
            execution.parentExecution != null && positions.value.isNotEmpty() && getSubTasksOfTaskAsList(
                getExecutionById(execution.parentExecution).taskId
            ).size - 1 > positions.value.last()

        //TODO: try removing execution.parentExecution != null by the Distributive property

        if (execution.parentExecution != null && hasBrothers && hasNextBrother) {
            addOneToLastPosition()

            val nextBrother =
                getSubTasksOfTaskAsList(getExecutionById(execution.parentExecution).taskId)[positions.value.last()]

            val parentExecution = getExecutionById(execution.parentExecution)

            start(nextBrother, parentExecution)
        } else {
            removeLastPosition()

            if (execution.parentExecution != null) { //has parent
                val parentExecution = getExecutionById(execution.parentExecution)
                checkExecution(
                    parentExecution, onEnd
                )
            } else {
                onEnd()
            }
        }

        Log.i("PlayActivatorScreen", "Salimos con la lista en ${positions.value}")
    }


    fun exit() {
        for (executionId in runningExecutionsIds.value) {
            updateExecution(
                executionId = executionId, end = unixEpochTime(), successfullyEnded = false
            )
        }
    }
}

data class PlayActivatorState(
    val tasks: List<Task> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    //TODO: Add other entities video 6/7
)