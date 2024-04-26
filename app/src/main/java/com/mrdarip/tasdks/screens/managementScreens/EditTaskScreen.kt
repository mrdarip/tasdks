package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    var iconEmoji by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(task) {
        name = task.name
        comment = task.comment ?: ""
        iconEmoji = task.iconEmoji ?: ""
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
        TextField(
            value = iconEmoji,
            onValueChange = { iconEmoji = it },
            label = { Text("Emoji") },
            placeholder = { Text("😃") }
        )

        TasksAndSubTasksList(
            mainMenuViewModel,
            tasks = mainMenuViewModel.getSubTasksOfTask(taskId ?: 0)
                .collectAsState(initial = emptyList()).value,
            0
        )
        Button(
            onClick = {
                mainMenuViewModel.upsertTask(
                    Task(
                        task.taskId,
                        name,
                        comment.ifBlank { null },
                        iconEmoji.ifBlank { null },
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

@Composable
fun TasksAndSubTasksList(mainMenuViewModel: EditTaskViewModel, tasks: List<Task>, level: Int) {
    var selectedItem by remember { mutableIntStateOf(0) }

    LazyRow {
        items(tasks) { task ->
            Card(modifier = Modifier.fillMaxWidth().clickable(onClick = {selectedItem = task.taskId?.toInt() ?: 0})) {
                Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp), modifier = Modifier.padding(16.dp)){
                    Text(
                        task.iconEmoji ?: "🤕",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Column {
                        Text(
                            text = task.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = task.comment ?: "NO DESCRIPTION",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        /*Text(
                            text = placeName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )*/
                    }
                }
            }
        }
    }

    if(selectedItem != 0) {
        TasksAndSubTasksList(
            mainMenuViewModel,
            mainMenuViewModel.getSubTasksOfTask(selectedItem.toLong())
                .collectAsState(initial = emptyList()).value,
            level + 1
        )
    }
}