package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.OrderTaskLiItem
import com.mrdarip.tasdks.composables.SelectTaskRow
import com.mrdarip.tasdks.composables.TaskFields
import com.mrdarip.tasdks.composables.TasksRow
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
fun EditTaskScreen(navController: NavController, taskId: Long) {
    val editTaskViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    EditTaskBodyContent(
        navController = navController, editTaskViewModel = editTaskViewModel, taskId = taskId
    )
}


@Composable
fun EditTaskBodyContent(
    navController: NavController, editTaskViewModel: EditTaskViewModel, taskId: Long
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val task by editTaskViewModel.getTaskById(taskId).collectAsState(
        initial = Task(
            name = "", comment = null, iconEmoji = null, placeId = null
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

    val parentTasks = editTaskViewModel.getParentTasksOfTask(taskId)
        .collectAsState(initial = emptyList()).value
    val subTasks =
        editTaskViewModel.getSubTasksOfTask(taskId).collectAsState(initial = emptyList()).value
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit task", style = MaterialTheme.typography.headlineMedium)
        TaskFields(taskName = name,
            taskEmoji = iconEmoji,
            taskComment = comment,
            onTaskNameChange = { name = it },
            onTaskEmojiChange = { iconEmoji = it },
            onTaskCommentChange = { comment = it })

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


        Button(onClick = {
            editTaskViewModel.upsertTask(
                Task(
                    task.taskId,
                    name,
                    comment.ifBlank { null },
                    iconEmoji.ifBlank { null },
                    task.placeId
                )
            )
            navController.popBackStack()
        }) {
            Icon(Icons.Filled.Edit, contentDescription = "Localized description")
            Text("Save")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTasksBottomSheet(
    onDismissRequest: () -> Unit = {},
    editTaskViewModel: EditTaskViewModel,
    taskId: Long,
    subTasks: List<Task>
) {

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState
    ) {
        var addingTask by remember { mutableStateOf(false) }
        var addingExistingTask by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(16.dp, 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (!addingTask) "Add task"
                else (if (addingExistingTask) "Adding existing task"
                else "Adding new task"), style = MaterialTheme.typography.headlineMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!addingTask || !addingExistingTask) {
                    Button(modifier = Modifier.weight(1f), onClick = {
                        addingTask = !addingTask
                        addingExistingTask = false
                    }) {
                        Text(if (!addingTask) "New task" else "Cancel")
                    }
                }

                if (!addingTask || addingExistingTask) {
                    Button(modifier = Modifier.weight(1f), onClick = {
                        addingTask = !addingTask
                        addingExistingTask = true
                    }) {
                        Text(if (!addingTask) "Existing task" else "Close")
                    }
                }
            }//TODO: the add task buttons should be at the end of the list, floating, maybe?

            if (addingTask) {
                if (addingExistingTask) {
                    SelectTaskRow(
                        tasks = editTaskViewModel.notSubtasksOfTask(taskId).collectAsState(initial = emptyList()).value,
                        onTaskClicked = {
                            editTaskViewModel.addTaskAsLastSubTask(
                                it.taskId,
                                taskId
                            )
                        })
                } else {

                    var name by remember { mutableStateOf("") }
                    var emoji by remember { mutableStateOf("") }
                    var comment by remember { mutableStateOf("") }

                    TaskFields(
                        taskName = name,
                        taskEmoji = emoji,
                        taskComment = comment,
                        onTaskNameChange = { name = it },
                        onTaskEmojiChange = { emoji = it },
                        onTaskCommentChange = { comment = it }
                    )

                    Button(onClick = {
                        editTaskViewModel.addTaskAsLastSubTask(
                            Task(
                                name = name,
                                iconEmoji = emoji.ifBlank { null },
                                comment = comment.ifBlank { null },
                                placeId = null
                            ),
                            taskId
                        )
                        addingTask = false
                    }) {
                        Text("Add task")
                    }
                }
            }


            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (subTasks.isNotEmpty() && !addingTask) {
                    item {
                        Text("Order tasks", style = MaterialTheme.typography.headlineMedium)
                    }

                    itemsIndexed(subTasks) { i, task ->
                        OrderTaskLiItem(
                            task = task,
                            position = i.toLong(),
                            maxPosition = subTasks.size.toLong(),
                            onMoveUpClicked = {
                                editTaskViewModel.decreaseTaskPosition(
                                    i.toLong(),
                                    taskId
                                )
                            },
                            onXclicked = {
                                editTaskViewModel.removeSubTask(
                                    parentTaskId = taskId,
                                    position = i.toLong()
                                )
                            },
                            onMoveDownClicked = {
                                editTaskViewModel.increaseTaskPosition(
                                    i.toLong(),
                                    taskId
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(modifier = Modifier.weight(1f), onClick = { }) {
            Text("New task")
        }
        Button(modifier = Modifier.weight(1f), onClick = {}) {
            Text("Existing task")
        }
    }
}

