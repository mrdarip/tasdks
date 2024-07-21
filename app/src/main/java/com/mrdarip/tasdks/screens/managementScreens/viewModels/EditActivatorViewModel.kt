package com.mrdarip.tasdks.screens.managementScreens.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditActivatorViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(EditActivatorState())
        private set

    init {
        getTasks()
    }

    private fun getTasks() {
        viewModelScope.launch {
            repository.activeTasks.collectLatest {
                state = state.copy(tasks = it)
            }
        }
    }


    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun upsertActivator(activator: Activator) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertActivator(activator)
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

    fun deleteActivator(activator: Activator) {
        viewModelScope.launch {
            repository.deleteActivator(activator)
        }
    }

    fun getTaskById(taskId: Long): Flow<Task>{
        return repository.getTaskByIdAsFlow(taskId)
    }

    fun getActivatorById(activatorId: Long): Flow<Activator>{
        return repository.getActivatorByIdAsFlow(activatorId)
    }

    fun getSubTasksOfTask(taskId: Long): Flow<List<Task>>{
        return repository.getSubTasksOfTask(taskId)
    }

    fun getParentTasksOfTask(taskId: Long): Flow<List<Task>>{
        return repository.getParentTasksOfTask(taskId)
    }

    //TODO oncategorychange,onitemcheckedchange
}

data class EditActivatorState(
    val tasks: List<Task> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    //TODO: Add other entities video 6/7
)