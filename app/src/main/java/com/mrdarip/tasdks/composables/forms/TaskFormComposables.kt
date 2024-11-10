package com.mrdarip.tasdks.composables.forms

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TasksRow
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreen
import com.mrdarip.tasdks.screens.managementScreens.EditTasksBottomSheet
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
@Preview
fun TaskFields(
    task: Task = Task(),
    onTaskChange: (Task) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextInput(
                modifier = Modifier.weight(3f),
                value = task.name,
                onValueChange = { onTaskChange(task.copy(name = it)) },
                label = "Name",
                placeholder = "Task name"
            )
            TextField(
                value = task.iconEmoji.orEmpty(),
                onValueChange = { onTaskChange(task.copy(iconEmoji = it.ifBlank { null })) },
                label = { Text("Emoji") },
                placeholder = { Text("😃") },
                modifier = Modifier.weight(1f),
                isError = !isValidEmoji(task.iconEmoji.orEmpty())
            )
        }
        TextInput(
            value = task.comment.orEmpty(),
            onValueChange = { onTaskChange(task.copy(comment = it.ifBlank { null })) },
            label = "Comment",
            placeholder = "Task comment",
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "is playlist")
            Checkbox(
                checked = task.canBeSkipped,
                onCheckedChange = { onTaskChange(task.copy(canBeSkipped = it)) }
            )
        }
        if (!task.canBeSkipped) {
            NumberInput(
                value = task.waitTime,
                onValidValueChange = {
                    Log.i("TaskFields", "onTaskChange: $it")
                    onTaskChange(task.copy(waitTime = it))
                },
                label = "Wait time",
                suffix = { Text("minutes") },
                placeholder = "Minutes to wait",
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "allow running parallel tasks")
                Checkbox(
                    checked = task.allowParallelTasks,
                    onCheckedChange = { onTaskChange(task.copy(allowParallelTasks = it)) }
                )
            }
        }
    }
}

@Composable
fun ExtendedTaskFields(
    parentTasks: List<Task>,
    subTasks: List<Task>,
    navController: NavController,
    editTaskViewModel: EditTaskViewModel,
    taskId: Long
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (parentTasks.isNotEmpty()) {
        Text(text = "Parent tasks", style = MaterialTheme.typography.headlineSmall)
        TasksRow(tasks = parentTasks, navController, showEditButton = false, onClickEdit = {
            showBottomSheet = true
        })//parent tasks
    }

    Text(text = "Subtasks", style = MaterialTheme.typography.headlineSmall)
    TasksRow(tasks = subTasks, navController, showEditButton = true, onClickEdit = {
        showBottomSheet = true
    }) //subtasks

    if (showBottomSheet) {
        EditTasksBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            editTaskViewModel = editTaskViewModel,
            taskId = taskId,
            subTasks = subTasks
        )
    }
}

@Composable
fun SavingTaskActionBar(
    editTaskViewModel: EditTaskViewModel,
    initialTask: Task,
    modifiedTask: Task,
    navController: NavController
) {
    Row {
        Button(onClick = {
            editTaskViewModel.upsertTask(
                initialTask.copy(
                    archived = !initialTask.archived
                )
            )
            navController.popBackStack()
        }) {
            Icon(Icons.Filled.Delete, contentDescription = "Localized description")
            Text(if (initialTask.archived) "Unarchive" else "Archive")
        }

        Button(onClick = {
            editTaskViewModel.upsertTask(
                modifiedTask
            )
            Log.i("SavingTaskActionBar", "onSave: $modifiedTask")
            navController.popBackStack()
        }) {
            Icon(Icons.Filled.Edit, contentDescription = "Localized description")
            Text("Save")
        }
        Button(onClick = {
            navController.navigate(
                "${AppScreen.CreateActivator.route}/${initialTask.taskId}"
            )
        }) {
            Icon(Icons.Filled.Add, contentDescription = "Localized description")
            Text("Create activator")
        }
    }
}