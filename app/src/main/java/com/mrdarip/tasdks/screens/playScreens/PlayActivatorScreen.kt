package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PlayActivatorScreen(activatorId: Long) {
    val editTaskViewModel = viewModel(modelClass = PlayActivatorViewModel::class.java)
    PlayActivatorBodyContent(
        topActivatorId = activatorId, viewModel = editTaskViewModel
    )
}

@Composable
fun PlayActivatorBodyContent(
    topActivatorId: Long, viewModel: PlayActivatorViewModel
) {
    val startedTasksNames = viewModel.startedTasksName.collectAsState().value
    var started by remember { mutableStateOf(false) }

    Column {

        if (started) {
            Text(text = "Current Task: ${viewModel.currentTask.collectAsState().value?.name}")
            Button(onClick = {
                Log.i("PlayActivatorScreen", "Check!")
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    checkExecution(
                        viewModel.getExecutionById(viewModel.currentExecutionId.value),
                        viewModel
                    )
                }
            }) {
                Text("Mark as Done")
            }
        }

        Button(onClick = {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                if (started) {
                    exit(viewModel)
                } else {
                    started = true
                    start(
                        viewModel.getTaskById(viewModel.getActivatorById(topActivatorId).taskToActivateId),
                        null,
                        viewModel
                    )
                }
            }
        }) {
            Text(if (started) "Exit" else "Start")
        }
    }
}


fun start(newTask: Task, parentExecution: Execution?, vm: PlayActivatorViewModel) {
    vm.setCurrentTask(newTask)
    Log.i("PlayActivatorScreen", "Tarea actual: ${newTask.name}")
    val currentExecutionId = vm.insertExecution(
        Execution(
            executionId = null, // for autoincrement
            start = unixEpochTime(),
            end = unixEpochTime(),
            successfullyEnded = false,
            activatorId = vm.topActivatorId.value, //TODO: Check is read only
            resourceId = null, //by now we don't implement resources
            parentExecution = parentExecution?.executionId,
            taskId = newTask.taskId ?: 0
        )
    )
    vm.setCurrentExecutionId(currentExecutionId)

    val currentTaskSubtasks = vm.getSubTasksOfTaskAsList(newTask.taskId ?: 0)
    if (currentTaskSubtasks.isNotEmpty()) {
        Log.i("PlayActivatorScreen", "Tenía hijos!")
        vm.appendPosition(0)
        start(currentTaskSubtasks[0], vm.getExecutionById(currentExecutionId), vm)
    }
}


fun checkExecution(execution: Execution, viewModel: PlayActivatorViewModel) {
    Log.i("PlayActivatorScreen", "Entramos con la lista en ${viewModel.positions.value}")
    viewModel.updateExecution(
        executionId = viewModel.currentExecutionId.value,
        end = unixEpochTime(),
        successfullyEnded = true
    )

    val hasBrothers = execution.parentExecution != null && viewModel.getSubTasksOfTaskAsList(
        viewModel.getExecutionById(
            execution.parentExecution
        ).taskId
    ).size > 1

    val hasNextBrother =
        execution.parentExecution != null && viewModel.positions.value.isNotEmpty() &&
                viewModel.getSubTasksOfTaskAsList(viewModel.getExecutionById(execution.parentExecution).taskId).size - 1 > viewModel.positions.value.last()

    //TODO: try removing execution.parentExecution != null by the Distributive property

    if (execution.parentExecution != null && hasBrothers && hasNextBrother) {
        viewModel.addOneToLastPosition()

        val nextBrother =
            viewModel.getSubTasksOfTaskAsList(viewModel.getExecutionById(execution.parentExecution).taskId)[viewModel.positions.value.last()]

        start(nextBrother, execution, viewModel)
    } else {
        viewModel.removeLastPosition()

        if (execution.parentExecution != null) { //has parent
            val parentExecution = viewModel.getExecutionById(execution.parentExecution)
            checkExecution(
                parentExecution, viewModel
            )
        } else {
            Log.d("PlayActivatorScreen", "All tasks are done")
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