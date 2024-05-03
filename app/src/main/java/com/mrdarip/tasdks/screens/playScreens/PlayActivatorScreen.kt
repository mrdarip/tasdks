package com.mrdarip.tasdks.screens.playScreens

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
        topActivatorId = activatorId,
        viewModel = editTaskViewModel
    )
}

@Composable
fun PlayActivatorBodyContent(
    topActivatorId: Long,
    viewModel: PlayActivatorViewModel
) {
    val startedTasksNames = viewModel.startedTasksName.collectAsState().value
    var started by remember { mutableStateOf(false) }

    Column {

        if (started) {
            Text(text = "Current Task: $startedTasksNames")
            Button(onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    check(viewModel)
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
        }
        ) {
            Text(if (started) "Exit" else "Start")
        }
    }
}


fun start(newTask: Task, parentExecution: Execution?, vm: PlayActivatorViewModel) {
    vm.setCurrentTask(newTask)

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

    val currentTaskSubtasks = vm.getSubTasksOfTaskAsList(newTask.taskId ?: 0)
    if (currentTaskSubtasks.isNotEmpty()) {
        positions.append(0)
        start(currentTaskSubtasks[0], vm.getExecutionById(currentExecutionId), vm)
    }
}


fun checkExecution(execution: Execution, viewModel: PlayActivatorViewModel) {
    viewModel.updateExecution(
        executionId = viewModel.currentExecutionId.value,
        end = unixEpochTime(),
        successfullyEnded = true
    )

    val hasBrothers = viewModel.getSubTasksOfTaskAsList(
        viewModel.getExecutionById(
            execution.parentExecution ?: 0
        ).taskId
    ).isNotEmpty()
    val hasNextBrother = parenttask.subtasks.size - 1 > positions.last
    if (hasBrothers && hasNextBrother) {
        positions.last++
        start(NextBrother)
    } else {
        positions.removeLast()

        val hasParent = execution.parentExecution != null
        if (hasParent) {
            val parentExecution = viewModel.getExecutionById(execution.parentExecution)
            checkExecution(
                parentExecution,
                viewModel
            )
        } else {
            //FINNISH!!!
        }
    }
}


fun exit(viewModel: PlayActivatorViewModel) {
    for (executionId in viewModel.runningExecutionsIds.value) {
        viewModel.updateExecution(
            executionId = executionId,
            end = unixEpochTime(),
            successfullyEnded = false
        )
    }
}

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}