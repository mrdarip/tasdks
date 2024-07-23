package com.mrdarip.tasdks.screens.managementScreens.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrdarip.tasdks.data.Graph
import com.mrdarip.tasdks.data.TasdksRepository
import com.mrdarip.tasdks.data.entity.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ManageResourcesViewModel(
    private val repository: TasdksRepository = Graph.repository
) : ViewModel() {
    var state by mutableStateOf(ManageResourcesState())
        private set

    init {
        getAllResources()
    }

    private fun getAllResources() {
        viewModelScope.launch {
            repository.allResources.collectLatest {
                state = state.copy(allResources = it)
            }
        }
    }
}

data class ManageResourcesState(
    val allResources: List<Resource> = emptyList()
)