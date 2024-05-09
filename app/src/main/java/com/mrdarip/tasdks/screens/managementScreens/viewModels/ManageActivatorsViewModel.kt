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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManageActivatorsViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(ManageActivatorsState())
        private set

    init {
        getActivators()
        getActiveActivators()
    }

    private fun getActivators() {
        viewModelScope.launch {
            repository.activators.collectLatest {
                state = state.copy(allActivators = it)
            }
        }
    }

    private fun getActiveActivators() {
        viewModelScope.launch {
            repository.activeActivators.collectLatest {
                state = state.copy(activeActivators = it)
            }
        }
    }

    fun getPlaceName(placeId: Long?): Flow<String> {
        return repository.getPlaceName(placeId)
    }

    fun getTaskById(taskId: Long): Flow<Task> {
        return repository.getTaskByIdAsFlow(taskId)
    }
}

data class ManageActivatorsState(
    val allActivators: List<Activator> = emptyList(),
    val activeActivators: List<Activator> = emptyList()
)