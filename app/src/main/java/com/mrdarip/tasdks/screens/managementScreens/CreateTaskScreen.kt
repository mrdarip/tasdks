package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TaskFields
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.screens.managementScreens.viewModels.CreateTaskViewModel

@Composable
fun CreateTaskScreen(navController: NavController) {
    val createTaskViewModel = viewModel(modelClass = CreateTaskViewModel::class.java)
    CreateTaskBodyContent(
        navController = navController,
        createTaskViewModel = createTaskViewModel
    )
}

@Composable
private fun CreateTaskBodyContent(
    navController: NavController,
    createTaskViewModel: CreateTaskViewModel
) {
    var task by remember { mutableStateOf(Task()) }
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Create task", style = MaterialTheme.typography.headlineLarge)
            TaskFields(
                task = task,
                onTaskChange = { task = it }
            )
        }
        Button(onClick = {
            createTaskViewModel.insertTask(task)
        }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task")
            Text("Add")
        }
    }

}

