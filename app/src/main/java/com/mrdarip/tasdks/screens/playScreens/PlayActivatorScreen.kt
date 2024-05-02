package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.foundation.layout.Column

import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
fun PlayActivatorScreen(activatorId: Long) {
    val editTaskViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    PlayActivatorBodyContent(
        topActivatorId = activatorId
    )
}

@Composable
fun PlayActivatorBodyContent(
    topActivatorId: Long
) {
    var startedTasksId = remember { mutableListOf<Long>() }
    var started by remember { mutableStateOf(false) }

    Column {
        Text(text = "Current Task: ")
        if (started) {
            Button(onClick = {
                currentExecutionSuccesfullyEnded = true
                currentTask = NextTask()
            }) {
                Text("Mark as Done")
            }
        }

        Button(onClick = {
            if (!started) {
                started = true
                startedTasksId =
                    currentExecutionStartTime = System.currentTimeMillis()
            }
            currentExecutionSuccesfullyEnded = false
            currentExecutionEndTime = System.currentTimeMillis()
            exitscreen
        }) {
            Text(if (started) "Exit" else "Start")
        }
    }
}

ACTIVADOR -> taskToActivateId = 1
1 limpiar el cuarto
	2 recoger la ropa del cuarto
		3 recoger ropa
		4 guardar ropa en el armario
	5 hacer la cama


var taskpos by remember{ mutableStateOf(0) }
startBtn-> start(ACTIVADOR.taskToActivateId, null)
fun start(currentTsk, parentExeId){
    currentTask = currentTsk
    currentExecutionId = insert(
        new execution (
            executionId = null, // for autoincrement
            start = now,
            end = now,
            successfullyEnded = false,
            activatorId = ACTIVADOR.id,
            resourceId = null, //by now we don't implement resources
            parentExecution = parentExeId,
            taskId = currentTask.id
        )
    )
    runningExecutionsIds.add(
        currentExecutionId
    )
    taskPositions.add(0)
    if (currentTask.hasSubTasks) {
        start(currentTask.SubTask, currentExecutionId)
    }
}

----------------------------
check ->
    update(
        executionId = currentExecutionId,
        end = now,
        successfullyEnded = true
    )
    runningExecutionsIds.remove(
        currentExecutionId
    )
    if (execution(currentExecutionId).parentExecution.task.SubTasksLen > taskpos) {
        taskpos++
        start(
            execution(currentExecutionId).parentExecution.task.SubTasks[taskpos],
            execution(currentExecutionId).parentExecution.executionId
        )
    }
else
{
    taskpos=0
}


exit ->