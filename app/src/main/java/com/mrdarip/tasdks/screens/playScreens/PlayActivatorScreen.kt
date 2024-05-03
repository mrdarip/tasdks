package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.foundation.layout.Column

import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    var startedTasksId = viewModel.startedTasksName.value
    var started by remember { mutableStateOf(false) }

    Column {

        if (started) {
            Text(text = "Current Task: ${viewModel.startedTasksName.value}")
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


fun start(currentTsk: Task, parentExeId: Long?, viewModel: PlayActivatorViewModel) {
    viewModel.addStartedTaskName(currentTsk.name)

    viewModel.setCurrentTaskId(currentTsk.taskId)
    val currentExecutionId = viewModel.insertExecution(
        Execution(
            executionId = null, // for autoincrement
            start = unixEpochTime(),
            end = unixEpochTime(),
            successfullyEnded = false,
            activatorId = viewModel.topActivatorId.value,
            resourceId = null, //by now we don't implement resources
            parentExecution = parentExeId,
            taskId = currentTsk.taskId ?: 0
        )
    )
    viewModel.setCurrentExecutionId(currentExecutionId)
    viewModel.addRunningExecutionsId(currentExecutionId)
    viewModel.addTaskPosition(0)

    val currentTaskSubtasks = viewModel.getSubTasksOfTaskAsList(currentTsk.taskId ?: 0)
    if (currentTaskSubtasks.isNotEmpty()) {
        start(currentTaskSubtasks[0], currentExecutionId, viewModel)
    }
}


suspend fun check(viewModel: PlayActivatorViewModel) {
    viewModel.removeLastStartedTaskName()

    viewModel.updateExecution(
        executionId = viewModel.currentExecutionId.value ?: 0,
        end = unixEpochTime(),
        successfullyEnded = true
    )
    viewModel.removeLastRunningExecutionId()

    if (viewModel.taskPositions.value.last() >= viewModel.getSubTasksOfTaskAsList(
            viewModel.getExecutionById(
                viewModel.getExecutionById(
                    viewModel.currentExecutionId.value ?: 0
                ).parentExecution //fixme: can be null
                    ?: 1
            ).taskId
        ).size
    ) {
        // if there is no more subTasks
        viewModel.removeLastTaskPositionId()
    }

    viewModel.addOneToLastTaskPosition()

    start(
        viewModel.getSubTasksOfTaskAsList(
            viewModel.getExecutionById(
                viewModel.getExecutionById(
                    viewModel.currentExecutionId.value ?: 0
                ).parentExecution ?: 0
            ).taskId
        )[viewModel.taskPositions.value.last()], //TODO: change getExecutionById(currentExecutionId) to currentExecution
        viewModel.getExecutionById(
            viewModel.getExecutionById(
                viewModel.currentExecutionId.value ?: 0
            ).parentExecution ?: 0
        ).executionId,
        viewModel
    )
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