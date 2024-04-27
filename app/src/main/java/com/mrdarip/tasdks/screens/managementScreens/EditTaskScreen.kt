package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel
import kotlinx.coroutines.launch

@Composable
fun EditTaskScreen(navController: NavController, taskId: Long?) {
    val mainMenuViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    EditTaskBodyContent(
        navController = navController,
        mainMenuViewModel = mainMenuViewModel,
        taskId = taskId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskBodyContent(
    navController: NavController,
    mainMenuViewModel: EditTaskViewModel,
    taskId: Long?
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var editSubTasks by remember { mutableStateOf(false) } // true for editing subtasks, false for editing parent tasks

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
        TasksRow(
            mainMenuViewModel,
            tasks = mainMenuViewModel.getParentTasksOfTask(taskId ?: 0)
                .collectAsState(initial = emptyList()).value,
            navController,
            onClickEdit = {
                editSubTasks = false
                showBottomSheet = true
            }
        )
        Text(text = task.name)
        Row {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                placeholder = { Text("Task name") }
            )
            TextField(
                value = iconEmoji,
                onValueChange = { iconEmoji = it },
                label = { Text("Emoji") },
                placeholder = { Text("😃") }
            )
        }
        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comment") },
            placeholder = { Text("Task comment") }
        )

        TasksRow(
            mainMenuViewModel,
            tasks = mainMenuViewModel.getSubTasksOfTask(taskId ?: 0)
                .collectAsState(initial = emptyList()).value,
            navController,
            onClickEdit = {
                editSubTasks = true
                showBottomSheet = true
            }
        ) //subtasks

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
            ) {
                val tasksToShow = (if (editSubTasks) mainMenuViewModel.getSubTasksOfTask(
                    taskId ?: 0
                ) else mainMenuViewModel.getParentTasksOfTask(
                    taskId ?: 0
                )).collectAsState(initial = emptyList()).value

                var showAddTaskOptions by remember { mutableStateOf(false) }

                LazyColumn(
                    modifier = Modifier.padding(0.dp, 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item() {//TODO: the add task buttons should be at the end of the list, floating
                        Column {
                            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                                showAddTaskOptions = !showAddTaskOptions
                            }) {
                                Text("Add task")
                            }

                            if (showAddTaskOptions) {
                                Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(16.dp)){
                                    Button(modifier = Modifier.weight(1f), onClick = { }) {
                                        Text("New task")
                                    }
                                    Button(modifier = Modifier.weight(1f), onClick = {}) {
                                        Text("Existing task")
                                    }
                                }
                            }
                        }

                    }
                    items(tasksToShow) { task ->
                        val placeName by mainMenuViewModel.getPlaceName(task.placeId)
                            .collectAsState(initial = "")
                        TaskLiItem(
                            task = task,
                            placeName = placeName,
                            onClick = {

                            }
                        )
                    }
                }

                // Sheet content
            }
        }


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
fun TasksRow(
    mainMenuViewModel: EditTaskViewModel,
    tasks: List<Task>,
    navController: NavController,
    onClickEdit: () -> Unit = {}
) {
    LazyRow {
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
                    Icon(Icons.Filled.Edit, contentDescription = "Edit task relation")
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
}


@Preview
@Composable
fun preview() {
    Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.spacedBy(16.dp)){
        Button(modifier = Modifier.weight(1f), onClick = { }) {
            Text("New task")
        }
        Button(modifier = Modifier.weight(1f), onClick = {}) {
            Text("Existing task")
        }
    }
}