package com.mrdarip.tasdks.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Execution
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WidgetBottomBarViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(CreateActivatorState())
        private set

    init {
        getActivators()
    }

    private fun getActivators() {
        viewModelScope.launch {
            repository.runningExecutions.collectLatest {
                state = state.copy(executions = it)
            }
        }
    }

}

data class CreateActivatorState(
    val executions: List<Execution> = emptyList()
    //TODO: Add other entities video 6/7
)