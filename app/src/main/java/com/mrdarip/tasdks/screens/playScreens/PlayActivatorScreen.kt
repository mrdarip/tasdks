package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlayActivatorScreen(activatorId: Long, navController: NavController) {
    Log.i("PlayActivatorScreen", "ActivatorId: $activatorId")
    val editTaskViewModel = viewModel(modelClass = PlayActivatorViewModel::class.java)
    editTaskViewModel.setTopActivatorId(activatorId)
    PlayActivatorBodyContent(
        topActivatorId = activatorId, viewModel = editTaskViewModel, navController = navController
    )
}

@Composable
fun PlayActivatorBodyContent(
    topActivatorId: Long, viewModel: PlayActivatorViewModel, navController: NavController
) {
    val startedTasksNames = viewModel.taskList.collectAsState().value.map { it.name }
    var started by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {

        if (started) {
            Text(text = "Current Task:\n${startedTasksNames.joinToString("\n")}")
            Button(onClick = {
                Log.i("PlayActivatorScreen", "Check!")
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    checkExecution(
                        viewModel.getExecutionById(viewModel.currentExecutionId.value),
                        viewModel,
                        onEnd = {
                            viewModel.viewModelScope.launch {
                                withContext(Dispatchers.Main) {
                                    navController.navigate(AppScreens.FirstScreen.route)
                                }
                            }
                        }
                    )
                }
            }) {
                Text("Mark as Done")
            }

            Button(onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    exit(viewModel)
                    withContext(Dispatchers.Main) {
                        navController.navigate(AppScreens.FirstScreen.route)
                    }
                }
            }) {
                Text("Exit")
            }
        } else {
            Button(onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    started = true
                    start(
                        viewModel.getTaskById(viewModel.getActivatorById(topActivatorId).taskToActivateId),
                        null,
                        viewModel
                    )
                }
            }) {
                Text("Start")
            }
        }
    }
}


fun start(newTask: Task, parentExecution: Execution?, vm: PlayActivatorViewModel) {
    vm.addToTasksList(newTask)
    Log.i("PlayActivatorScreen", "Tarea actual: ${newTask.name}")
    val currentExecutionId = vm.insertExecution(
        Execution(
            start = unixEpochTime(),
            end = unixEpochTime(),
            successfullyEnded = false,
            activatorId = vm.topActivatorId.value, //TODO: Check is read only
            resourceId = null, //by now we don't implement resources
            parentExecution = parentExecution?.executionId,
            taskId = newTask.taskId
        )
    )
    vm.setCurrentExecutionId(currentExecutionId)

    val currentTaskSubtasks = vm.getSubTasksOfTaskAsList(newTask.taskId)
    if (currentTaskSubtasks.isNotEmpty()) {
        Log.i("PlayActivatorScreen", "Tenía hijos!")
        vm.appendPosition(0)
        start(currentTaskSubtasks[0], vm.getExecutionById(currentExecutionId), vm)
    }
}


fun checkExecution(execution: Execution, viewModel: PlayActivatorViewModel, onEnd: () -> Unit) {
    Log.i("PlayActivatorScreen", "Check ${execution.taskId} with id ${execution.executionId}")
    Log.i("PlayActivatorScreen", "Entramos con la lista en ${viewModel.positions.value}")


    viewModel.removeFromTaskList()

    viewModel.updateExecution(
        executionId = execution.executionId,
        end = unixEpochTime(),
        successfullyEnded = true
    )

    val hasBrothers = execution.parentExecution != null && viewModel.getSubTasksOfTaskAsList(
        viewModel.getExecutionById(
            execution.parentExecution
        ).taskId
    ).size > 1

    val hasNextBrother =
        execution.parentExecution != null && viewModel.positions.value.isNotEmpty() && viewModel.getSubTasksOfTaskAsList(
            viewModel.getExecutionById(execution.parentExecution).taskId
        ).size - 1 > viewModel.positions.value.last()

    //TODO: try removing execution.parentExecution != null by the Distributive property

    if (execution.parentExecution != null && hasBrothers && hasNextBrother) {
        viewModel.addOneToLastPosition()

        val nextBrother =
            viewModel.getSubTasksOfTaskAsList(viewModel.getExecutionById(execution.parentExecution).taskId)[viewModel.positions.value.last()]

        val parentExecution = viewModel.getExecutionById(execution.parentExecution)

        start(nextBrother, parentExecution, viewModel)
    } else {
        viewModel.removeLastPosition()

        if (execution.parentExecution != null) { //has parent
            val parentExecution = viewModel.getExecutionById(execution.parentExecution)
            checkExecution(
                parentExecution, viewModel, onEnd
            )
        } else {
            onEnd()
        }
    }

    Log.i("PlayActivatorScreen", "Salimos con la lista en ${viewModel.positions.value}")
}


fun exit(viewModel: PlayActivatorViewModel) {
    for (executionId in viewModel.runningExecutionsIds.value) {
        viewModel.updateExecution(
            executionId = executionId, end = unixEpochTime(), successfullyEnded = false
        )
    }
}

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}