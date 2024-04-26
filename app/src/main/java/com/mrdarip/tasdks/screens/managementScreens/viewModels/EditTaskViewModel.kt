package com.mrdarip.tasdks.screens.managementScreens.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Place
import com.mrdarip.tasdks.data.entity.Object
import com.mrdarip.tasdks.data.entity.Task

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
class EditTaskViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(EditTaskState())
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

    fun getTaskById(taskId: Long): Flow<Task>{
        return repository.getTaskById(taskId)
    }

    fun getSubTasksOfTask(taskId: Long): Flow<List<Task>>{
        return repository.getSubTasksOfTask(taskId)
    }

    fun getParentTasksOfTask(taskId: Long): Flow<List<Task>>{
        return repository.getParentTasksOfTask(taskId)
    }

    //TODO oncategorychange,onitemcheckedchange
}

data class EditTaskState(
    val tasks: List<Task> = emptyList(),
    val objects: List<Object> = emptyList(),
    val places: List<Place> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    //TODO: Add other entities video 6/7
)