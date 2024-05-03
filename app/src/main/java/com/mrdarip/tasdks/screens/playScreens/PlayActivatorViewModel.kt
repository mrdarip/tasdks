package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Object
import com.mrdarip.tasdks.data.entity.Place
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayActivatorViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    private val _startedTasksName = MutableStateFlow<MutableList<String>>(mutableListOf())
    val startedTasksName = _startedTasksName.asStateFlow()
    fun addStartedTaskName(taskName: String) {
        _startedTasksName.value.add(taskName)
    }

    fun removeLastStartedTaskName() {
        _startedTasksName.value.removeLast()
    }

    private val _topActivatorId = MutableStateFlow<Long>(-1)
    val topActivatorId = _topActivatorId.asStateFlow()
    fun setTopActivatorId(activatorId: Long) {
        _topActivatorId.value = activatorId
    }

    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask = _currentTask.asStateFlow()
    fun setCurrentTask(task: Task) {
        _currentTask.value = task
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

    private val _taskPositions = MutableStateFlow<List<Int>>(emptyList())
    val taskPositions = _taskPositions.asStateFlow()
    fun addTaskPosition(position: Int) {
        _taskPositions.value += position
    }

    fun removeLastTaskPositionId() {
        _taskPositions.value = _taskPositions.value.dropLast(1)
    }

    fun addOneToLastTaskPosition() {
        val lastPosition = _taskPositions.value.last()
        _taskPositions.value = _taskPositions.value.dropLast(1) + (lastPosition + 1)
    }

    var state by mutableStateOf(PlayActivatorState())
        private set

    fun getPlaceName(placeId: Long?): Flow<String> {
        return repository.getPlaceName(placeId)
    }

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

    fun getExecutionById(executionId: Long): Execution {
        return repository.getExecutionById(executionId)
    }
}

data class PlayActivatorState(
    val tasks: List<Task> = emptyList(),
    val objects: List<Object> = emptyList(),
    val places: List<Place> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    //TODO: Add other entities video 6/7
)