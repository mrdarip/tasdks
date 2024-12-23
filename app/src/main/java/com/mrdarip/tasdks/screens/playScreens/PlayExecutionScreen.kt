package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun PlayExecutionScreen(executionId: Long, navController: NavController) {
    val playExecutionViewModel = viewModel(modelClass = PlayExecutionViewModel::class.java)
    playExecutionViewModel.setTopExecution(executionId)
    PlayActivatorBodyContent(
        viewModel = playExecutionViewModel
    )
}

@Composable
private fun PlayActivatorBodyContent(
    viewModel: PlayExecutionViewModel
) {
    if (viewModel.state.topExecution != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.state.actualExecution != viewModel.state.topExecution) {
                // Row for title
                Row {
                    Text(viewModel.state.topExecution!!.task.name)
                }

                Icon(Icons.Default.MoreVert, contentDescription = "...")
                IconButton(onClick = { }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "view parents")
                }
                Icon(Icons.Default.MoreVert, contentDescription = "...")
            }

            TaskPlayer(viewModel)
        }
    } else {
        Text("Loading..., topExecution is null")
    }
}

@Composable
private fun TaskPlayer(viewModel: PlayExecutionViewModel) {
    Column(horizontalAlignment = Alignment.Start) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row {
                if (viewModel.state.actualExecution.task.iconEmoji != null) {
                    Text(viewModel.state.actualExecution.task.iconEmoji!!)
                }
                Text(viewModel.state.actualExecution.task.name)
            }

            // Row for actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(onClick = { viewModel.undoExecution() }) {
                    Icon(Icons.Default.Share, contentDescription = "go back")
                }
                IconButton(onClick = { viewModel.completeExecution() }) {
                    if (viewModel.state.actualExecution.user.isRunning()) {
                        Icon(Icons.Default.Done, contentDescription = "complete")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = "start")
                    }
                }
                IconButton(onClick = { TODO("implement skip modal") }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "skip")
                }
            }
        }
        if (viewModel.state.nextTask != null) {
            Text("Next: ${viewModel.state.nextTask!!.name}")
        }
    }
}