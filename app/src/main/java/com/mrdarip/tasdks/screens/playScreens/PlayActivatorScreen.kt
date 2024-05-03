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
    vm.addStartedTaskName(newTask.name)

    vm.setCurrentTask(newTask) //not used??
    val currentExecutionId = vm.insertExecution(
        Execution(
            executionId = null, // for autoincrement
            start = unixEpochTime(),
            end = unixEpochTime(),
            successfullyEnded = false,
            activatorId = vm.topActivatorId.value,
            resourceId = null, //by now we don't implement resources
            parentExecution = parentExecution?.executionId,
            taskId = newTask.taskId ?: 0
        )
    )
    vm.setCurrentExecutionId(currentExecutionId)
    vm.addRunningExecutionsId(currentExecutionId)
    vm.addTaskPosition(0)

    val currentTaskSubtasks = vm.getSubTasksOfTaskAsList(newTask.taskId ?: 0)
    if (currentTaskSubtasks.isNotEmpty()) {
        start(currentTaskSubtasks[0], vm.getExecutionById(currentExecutionId), vm)
    }
}


fun check(viewModel: PlayActivatorViewModel) {
    viewModel.removeLastStartedTaskName()

    viewModel.updateExecution(
        executionId = viewModel.currentExecutionId.value,
        end = unixEpochTime(),
        successfullyEnded = true
    )
    viewModel.removeLastRunningExecutionId()

    val currentExecution = viewModel.getExecutionById(
        viewModel.currentExecutionId.value //can't be null (-1) as checked can only be called if started has been run before
    )

    val currentExeParentExe = currentExecution.parentExecution?.let {
        viewModel.getExecutionById(it)
    }

    if (currentExeParentExe != null) {
        val currentTaskBrothers = viewModel.getSubTasksOfTaskAsList(
            currentExeParentExe.taskId
        )

        if (viewModel.taskPositions.value.last() >= currentTaskBrothers.size - 1) {
            // if there is no more subTasks
            viewModel.removeLastTaskPositionId()
        }

        viewModel.addOneToLastTaskPosition()

        start(
            currentTaskBrothers[viewModel.taskPositions.value.last()],
            currentExeParentExe,
            viewModel
        )
    } else {
        //Todo: implement the exit case
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