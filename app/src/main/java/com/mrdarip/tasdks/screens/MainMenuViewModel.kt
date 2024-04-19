package com.mrdarip.tasdks.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainMenuViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(MainMenuState())
        private set

    init {
        getTasks()
        getPlaces()
        getTasksOrdered()
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
            repository.tasks.collectLatest {
                state = state.copy(tasks = it)
            }
        }
    }

    private fun getTasksOrdered() {
        viewModelScope.launch {
            getTasksOrderByLastDone()
            getTasksOrderByUsuallyAtThisTime()
        }
    }

    private suspend fun getTasksOrderByUsuallyAtThisTime() {
        repository.tasksOrderByUsuallyAtThisTime.collectLatest {
            state = state.copy(tasksOrderedByUsuallyAtThisTime = it)
        }

    }

    private suspend fun getTasksOrderByLastDone() {
        repository.tasksOrderByLastDone.collectLatest {
            state = state.copy(tasksOrderedByLastDone = it)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    //TODO oncategorychange,onitemcheckedchange
}

data class MainMenuState(
    val tasks: List<Task> = emptyList(),
    val tasksOrderedByLastDone: List<Task> = emptyList(),
    val tasksOrderedByUsuallyAtThisTime: List<Task> = emptyList(),
    val places: List<Place> = emptyList(),
    //TODO: Add other entities video 6/7
)