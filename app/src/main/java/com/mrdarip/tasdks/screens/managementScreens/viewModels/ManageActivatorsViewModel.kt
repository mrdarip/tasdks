package com.mrdarip.tasdks.screens.managementScreens.viewModels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Object
import com.mrdarip.tasdks.data.entity.Place
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManageActivatorsViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(ManageActivatorsState())
        private set

    init {
        getTasks()
        getPlaces()
        getActivators()
        getTasksOrderByUsuallyAtThisTime()
    }

    private fun getTasks() {
        viewModelScope.launch {
            repository.tasks.collectLatest {
                state = state.copy(tasks = it)
            }
        }
    }

    private fun getPlaces() {
        viewModelScope.launch {
            repository.places.collectLatest {
                state = state.copy(places = it)
            }
        }
    }

    private fun getObjects() {
        viewModelScope.launch {
            repository.objects.collectLatest {
                state = state.copy(objects = it)
            }
        }
    }

    private fun getTasksOrderByUsuallyAtThisTime() {
        viewModelScope.launch {
            repository.tasksOrderByUsuallyAtThisTime.collectLatest {
                state = state.copy(tasksOrderedByUsuallyAtThisTime = it)
            }
        }

    }

    private fun getActivators() {
        viewModelScope.launch {
            repository.activators.collectLatest {
                state = state.copy(activators = it)
            }
        }
    }

    fun getPlaceName(placeId: Long?): Flow<String> {
        return repository.getPlaceName(placeId)
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun getTaskById(taskId: Long): Flow<Task> {
        return repository.getTaskByIdAsFlow(taskId)
    }

    fun insertActivator(activator: Activator): Long {
        return repository.insertActivator(activator)
    }
}

data class ManageActivatorsState(
    val tasks: List<Task> = emptyList(),
    val objects: List<Object> = emptyList(),
    val places: List<Place> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    val activators: List<Activator> = emptyList()
    //TODO: Add other entities video 6/7
)