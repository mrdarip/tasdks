package com.mrdarip.tasdks.screens.bottomBarScreens.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainMenuViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(MainMenuState())
        private set

    init {
        getOverdueTasks()
        getPendingTasks()
        getActiveTasks()
        getTasksOrderByLastDone()
        getTasksOrderByUsuallyAtThisTime()
    }

    private fun getActiveTasks() {
        viewModelScope.launch {
            repository.activeTasks.collectLatest {
                state = state.copy(activeTasks = it)
            }
        }
    }

    private fun getOverdueTasks() {
        viewModelScope.launch {
            repository.overdueTasks.collectLatest {
                state = state.copy(overdueActivators = it)
            }
        }
    }

    private fun getPendingTasks() {
        viewModelScope.launch {
            repository.pendingTasks.collectLatest {
                state = state.copy(pendingActivators = it)
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

    private fun getTasksOrderByLastDone() {
        viewModelScope.launch {
            repository.tasksOrderByLastDone.collectLatest {
                state = state.copy(tasksOrderedByLastDone = it)
            }
        }
    }


    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun getTaskETA(taskId: Long): Flow<Long> {
        return repository.getTaskETA(taskId)
    }

    fun getTaskById(taskId: Long): Flow<Task> {
        return repository.getTaskByIdAsFlow(taskId)
    }

    fun insertActivator(activator: Activator): Long {
        return repository.insertActivator(activator)
    }
}

data class MainMenuState(
    val activeTasks: List<Task> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    val overdueActivators: List<Activator> = emptyList(),
    val pendingActivators: List<Activator> = emptyList(),
    //TODO: Add other entities video 6/7
)