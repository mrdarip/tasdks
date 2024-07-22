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
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlayActivatorScreen(activatorId: Long, navController: NavController) {
    Log.i("PlayActivatorScreen", "ActivatorId: $activatorId")
    val editTaskViewModel = viewModel(modelClass = PlayActivatorViewModel::class.java)
    editTaskViewModel.setTopActivatorId(activatorId)
    val topActivator = editTaskViewModel.getActivatorById(activatorId)
    PlayActivatorBodyContent(
        topActivator = topActivator, viewModel = editTaskViewModel, navController = navController
    )
}

@Composable
fun PlayActivatorBodyContent(
    topActivator: Activator, viewModel: PlayActivatorViewModel, navController: NavController
) {
    val startedTasksNames = viewModel.taskList.collectAsState().value.map { it.name }
    var started by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {

        if (started) {
            Text(text = "Current Task:\n${startedTasksNames.joinToString("\n")}")
            Button(onClick = {
                Log.i("PlayActivatorScreen", "Check!")
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.checkExecution(
                        viewModel.getExecutionById(viewModel.currentExecutionId.value),
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
                    viewModel.exit()
                    withContext(Dispatchers.Main) {
                        navController.navigate(AppScreens.FirstScreen.route)
                    }
                }
            }) {
                Text("Exit")
            }
        } else {
            val topActivatorTask = viewModel.getTaskById(topActivator.taskToActivateId)
            Text(text = "Activator's task: ${topActivatorTask.name}")
            Button(onClick = {
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    started = true
                    viewModel.start(
                        topActivatorTask,
                        null
                    )
                }
            }) {
                Text("Start")
            }
        }
    }
}

fun unixEpochTime(): Int {
    return (System.currentTimeMillis() / 1000).toInt()
}