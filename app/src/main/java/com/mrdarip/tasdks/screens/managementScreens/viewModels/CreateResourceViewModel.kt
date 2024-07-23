package com.mrdarip.tasdks.screens.managementScreens.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Resource
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CreateResourceViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(CreateTaskState())
        private set


    fun insertResource(resource: Resource): Long {
        var newResourceId = 0L
        viewModelScope.launch {
            newResourceId = repository.insertResource(resource)
        }
        return newResourceId
    }

    fun getTaskById(taskId: Long): Flow<Task> {
        return repository.getTaskByIdAsFlow(taskId)
    }

}

data class CreateResourceState(
    val tasks: List<Task> = emptyList()
)