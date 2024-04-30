package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreens
import com.mrdarip.tasdks.screens.bottomBarScreens.TaskCard
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
fun EditTaskScreen(navController: NavController, taskId: Long?) {
    val editTaskViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    EditTaskBodyContent(
        navController = navController, editTaskViewModel = editTaskViewModel, taskId = taskId
    )
}


@Composable
fun EditTaskBodyContent(
    navController: NavController, editTaskViewModel: EditTaskViewModel, taskId: Long?
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var editSubTasks by remember { mutableStateOf(false) } // true for editing subtasks, false for editing parent tasks

    val task by editTaskViewModel.getTaskById(taskId ?: 0).collectAsState(
        initial = Task(
            taskId = null, name = "", comment = null, iconEmoji = null, placeId = null
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

    val parentTasks = editTaskViewModel.getParentTasksOfTask(taskId ?: 0)
        .collectAsState(initial = emptyList()).value
    val subTasks =
        editTaskViewModel.getSubTasksOfTask(taskId ?: 0).collectAsState(initial = emptyList()).value
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

        Text(text = "Parent tasks", style = MaterialTheme.typography.headlineSmall)
        TasksRow(tasks = parentTasks, navController, onClickEdit = {
            editSubTasks = false
            showBottomSheet = true
        })//parent tasks //TODO: parent tasks order shouldn't be editable

        Text(text = "Subtasks", style = MaterialTheme.typography.headlineSmall)
        TasksRow(tasks = subTasks, navController, onClickEdit = {
            editSubTasks = true
            showBottomSheet = true
        }) //subtasks

        if (showBottomSheet) {
            EditTasksBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                editTaskViewModel = editTaskViewModel,
                taskId = taskId,
                tasksToShow = subTasks
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

@Composable
fun TasksRow(
    tasks: List<Task>, navController: NavController, onClickEdit: () -> Unit = {}
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onClickEdit() })
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit tasks order")
                    Column {
                        Text(
                            text = "Edit",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
        items(tasks) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { navController.navigate(AppScreens.EditTask.route + "/${task.taskId ?: -1}") })
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
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
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTasksBottomSheet(
    onDismissRequest: () -> Unit = {},
    editTaskViewModel: EditTaskViewModel,
    taskId: Long?,
    tasksToShow: List<Task>
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
                        Text(if (!addingTask) "Existing task" else "Cancel")
                    }
                }
            }//TODO: the add task buttons should be at the end of the list, floating, maybe?

            if (addingTask) {
                if (addingExistingTask) {

                    SelectTaskColumn(
                        tasks = tasksToShow,
                        onTaskClicked = {})//TODO: add all tasks list

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
                                taskId = null,
                                name = name,
                                iconEmoji = emoji.ifBlank { null },
                                comment = comment.ifBlank { null },
                                placeId = null
                            ),
                            taskId ?: 0
                        )
                    }) {
                        Text("Add task")
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (tasksToShow.isNotEmpty()) {
                    item {
                        Text("Order tasks", style = MaterialTheme.typography.headlineMedium)
                    }
                    itemsIndexed(tasksToShow) { i,task->
                        OrderTaskLiItem(
                            task, taskId,i.toLong(), editTaskViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderTaskLiItem(task: Task, parentTaskId: Long?, position:Long,editTaskViewModel: EditTaskViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                task.iconEmoji ?: "🤕",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    editTaskViewModel.decreaseTaskPosition(
                        position,
                        parentTaskId ?: 0
                    )
                }) { //TODO: check button works without launching exceptions, and check query is working
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Move task order up"
                    )
                }
                Text(
                    text = task.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    editTaskViewModel.increaseTaskPosition(
                        position,
                        parentTaskId ?: 0
                    )
                }) { //TODO: check button works without launching exceptions, and check query is working
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Move task order down"
                    )
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

@Composable
fun SelectTaskColumn(
    tasks: List<Task>,
    onTaskClicked: () -> Unit
) {//TODO: review this function, adding a searchbar
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(tasks) { task ->
            TaskCard(task = task, placeName = "", onClick = onTaskClicked)
        }
    }
}