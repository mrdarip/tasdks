package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskState
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
fun EditTaskScreen(navController: NavController, taskId: Long?) {
    val mainMenuViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    EditTaskBodyContent(
        mainMenuViewModel = mainMenuViewModel,
        mainMenuState = mainMenuState,
        taskId = taskId
    )
}

@Composable
fun EditTaskBodyContent(
    mainMenuViewModel: EditTaskViewModel,
    mainMenuState: EditTaskState,
    taskId: Long?
) {
    val task by mainMenuViewModel.getTaskById(taskId ?: 0).collectAsState(
        initial = Task(
            taskId = null,
            name = "",
            comment = "",
            iconEmoji = "",
            placeId = null
        )
    )
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(text = task.name)
        Text(text = task.taskId.toString())
        var text by rememberSaveable { mutableStateOf(task.name) }

        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            label = { Text("Email") },
            placeholder = { Text("example@gmail.com") }
        )
        Button(
            onClick = { mainMenuViewModel.upsertTask(Task(task.taskId,text,task.comment,task.iconEmoji,task.placeId)) },
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
        ) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Localized description",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Like")
        }

    }
}