package com.mrdarip.tasdks.screens.managementScreens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.OrderTaskLiItem
import com.mrdarip.tasdks.composables.SelectTaskGrid
import com.mrdarip.tasdks.composables.forms.ExtendedTaskFields
import com.mrdarip.tasdks.composables.forms.SavingTaskActionBar
import com.mrdarip.tasdks.composables.forms.TaskFields
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
private fun EditTaskBodyContent(
    navController: NavController, editTaskViewModel: EditTaskViewModel, taskId: Long
) {
    val initialTask by editTaskViewModel.getTaskById(taskId).collectAsState(
        initial = Task(
            name = "", comment = null, iconEmoji = null
        )
    )

    var modifiedTask by remember {
        mutableStateOf(initialTask)
    }
    LaunchedEffect(initialTask) {
        modifiedTask = initialTask
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
        TaskFields(
            modifiedTask,
            onTaskChange = {
                Log.i("EditTaskScreen", "Task changed: $it")
                modifiedTask = it
            } //TODO: upsert only when the save button is clicked
        )

        ExtendedTaskFields(
            parentTasks = parentTasks,
            subTasks = subTasks,
            navController = navController,
            editTaskViewModel = editTaskViewModel,
            taskId = taskId
        )

        SavingTaskActionBar(editTaskViewModel, initialTask, modifiedTask, navController)
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
                    SelectTaskGrid(
                        tasks = editTaskViewModel.notSubtasksOfTask(taskId)
                            .collectAsState(initial = emptyList()).value,
                        onTaskClicked = {
                            editTaskViewModel.addTaskAsLastSubTask(
                                it.taskId,
                                taskId
                            )
                        })
                } else {

                    var newTask by remember { mutableStateOf(Task()) }

                    TaskFields(
                        task = newTask,
                        onTaskChange = { newTask = it }
                    )

                    Button(onClick = {
                        editTaskViewModel.addTaskAsLastSubTask(
                            newTask,
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

