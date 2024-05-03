package com.mrdarip.tasdks.screens.managementScreens.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Object
import com.mrdarip.tasdks.data.entity.Place
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.Dispatchers
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

    fun addTaskAsLastSubTask(taskId: Long, parentTaskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTaskAsLastSubTask(taskId, parentTaskId)
        }
    }

    fun addTaskAsLastSubTask(task: Task, parentTaskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertTask(task)
            repository.addTaskAsLastSubTask(
                id,
                parentTaskId
            )
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

    fun getTaskById(taskId: Long): Flow<Task> {
        return repository.getTaskByIdAsFlow(taskId)
    }

    fun getSubTasksOfTask(taskId: Long): Flow<List<Task>> {
        return repository.getSubTasksOfTask(taskId)
    }

    fun getParentTasksOfTask(taskId: Long): Flow<List<Task>> {
        return repository.getParentTasksOfTask(taskId)
    }

    fun increaseTaskPosition(position: Long, parentId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            repository.increaseTaskPosition(position,parentId)
        }
    }

    fun decreaseTaskPosition(position: Long, parentId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            repository.decreaseTaskPosition(position,parentId)
        }
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