package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
fun CreateTaskBodyContent(navController: NavController, createTaskViewModel: CreateTaskViewModel) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.SpaceAround) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Create task", style = MaterialTheme.typography.headlineLarge)
            TaskFields(
                taskName = name,
                taskEmoji = emoji,
                taskComment = comment,
                onTaskNameChange = { name = it },
                onTaskEmojiChange = { emoji = it },
                onTaskCommentChange = { comment = it }
            )
        }
        Button(onClick = {
            createTaskViewModel.insertTask(
                Task(
                    taskId = null,
                    name = name,
                    comment = comment.ifBlank { null },
                    iconEmoji = emoji.ifBlank { null },
                    placeId = null
                )
            )
        }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task")
            Text("Add")
        }
    }

}

@Preview
@Composable
fun TaskFields(
    taskName: String = "test",
    taskEmoji: String = "🍀",
    taskComment: String = "comment test",
    onTaskNameChange: (String) -> Unit = {},
    onTaskEmojiChange: (String) -> Unit = {},
    onTaskCommentChange: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = taskName,
                onValueChange = { onTaskNameChange(it) },
                label = { Text("Name") },
                placeholder = { Text("Task name") },
                modifier = Modifier.weight(3f)
            )
            TextField(
                value = taskEmoji,
                onValueChange = { onTaskEmojiChange(it) },
                label = { Text("Emoji") },
                placeholder = { Text("😃") },
                modifier = Modifier.weight(1f)
            )
        }
        TextField(
            value = taskComment,
            onValueChange = { onTaskCommentChange(it) },
            label = { Text("Comment") },
            placeholder = { Text("Task comment") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}