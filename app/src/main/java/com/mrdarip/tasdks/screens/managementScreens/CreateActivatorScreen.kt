package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.ActivatorFields
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.RepetitionRange
import com.mrdarip.tasdks.data.entity.RepetitionType
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.screens.managementScreens.viewModels.CreateActivatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateActivatorScreen(navController: NavController) {
    val createActivatorViewModel = viewModel(modelClass = CreateActivatorViewModel::class.java)
    CreateActivatorBodyContent(
        navController = navController,
        createActivatorViewModel = createActivatorViewModel
    )
}

@Composable
fun CreateActivatorBodyContent(
    navController: NavController,
    createActivatorViewModel: CreateActivatorViewModel
) {
    var comment by remember { mutableStateOf("") }
    var task by remember { mutableStateOf<Task?>(null) }
    Column(verticalArrangement = Arrangement.SpaceAround) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Create task", style = MaterialTheme.typography.headlineLarge)
            ActivatorFields(
                activatorComment = comment,
                possibleTasksToActivate = createActivatorViewModel.state.tasks,
                onCommentChange = { comment = it },
                taskToActivate = task,
                onTaskToActivateChange = { task = it }

            )
        }
        Button(onClick = {
            createActivatorViewModel.viewModelScope.launch(Dispatchers.IO) {
                createActivatorViewModel.insertActivator(
                    Activator(
                        comment = comment.ifBlank { null },
                        taskToActivateId = task?.taskId ?: 0,
                        endAfterDate = null,
                        userCancelled = false,
                        repetitionRange = RepetitionRange(0, 0, 0, RepetitionType.YEARS),
                        endAfterRep = 0

                    )
                )
            }
        }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task")
            Text("Add")
        }
    }

}

