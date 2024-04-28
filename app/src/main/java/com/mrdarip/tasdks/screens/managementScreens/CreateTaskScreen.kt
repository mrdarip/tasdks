package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
fun CreateTaskScreen(navController: NavController) {
    val editTaskViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    CreateTaskBodyContent(
        navController = navController,
        editTaskViewModel = editTaskViewModel
    )
}

@Composable
fun CreateTaskBodyContent(navController: NavController, editTaskViewModel: EditTaskViewModel) {

}

@Composable
fun TaskFields(
    taskName: String,
    taskEmoji: String,
    taskComment: String,
    onTaskNameChange: (String) -> Unit,
    onTaskEmojiChange: (String) -> Unit,
    onTaskCommentChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = taskName,
            onValueChange = {onTaskNameChange(it) },
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
        onValueChange = { onTaskCommentChange(it)},
        label = { Text("Comment") },
        placeholder = { Text("Task comment") },
        modifier = Modifier.fillMaxWidth()
    )
}