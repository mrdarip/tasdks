package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
fun EditTaskScreen(navController: NavController, taskId: Long?) {
    val mainMenuViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    EditTaskBodyContent(
        navController = navController,
        mainMenuViewModel = mainMenuViewModel,
        taskId = taskId
    )
}

@Composable
fun EditTaskBodyContent(
    navController: NavController,
    mainMenuViewModel: EditTaskViewModel,
    taskId: Long?
) {
    val task by mainMenuViewModel.getTaskById(taskId ?: 0).collectAsState(
        initial = Task(
            taskId = null,
            name = "",
            comment = null,
            iconEmoji = null,
            placeId = null
        )
    )
    var name by rememberSaveable { mutableStateOf("") }
    var comment by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(task) {
        name = task.name
        comment = task.comment ?: ""
    }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(text = task.name)
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            placeholder = { Text("Task name") }
        )
        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comment") },
            placeholder = { Text("Task comment") }
        )
        Button(
            onClick = {
                mainMenuViewModel.upsertTask(
                    Task(
                        task.taskId,
                        name,
                        comment.ifBlank { null },
                        task.iconEmoji,
                        task.placeId
                    )
                )
                navController.popBackStack()
            }
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = "Localized description")
            Text("Like")
        }
    }
}