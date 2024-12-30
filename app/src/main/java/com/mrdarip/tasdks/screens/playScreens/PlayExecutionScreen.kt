package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.EndReason
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.navigation.AppScreen

@Composable
fun PlayExecutionScreen(navigationArgs: Execution, navController: NavController) {
    val playExecutionViewModel = viewModel(modelClass = PlayExecutionViewModel::class.java)

    val completedExecutions by playExecutionViewModel.completedExecutions.observeAsState(initial = false)
    LaunchedEffect(completedExecutions) {
        if (completedExecutions) {
            navController.navigate(AppScreen.NotFound.route)
        }
    }

    playExecutionViewModel.setTopExecution(navigationArgs)
    PlayActivatorBodyContent(
        viewModel = playExecutionViewModel
    )
}

@Composable
private fun PlayActivatorBodyContent(
    viewModel: PlayExecutionViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.state.topExecution != null) {
            val actualParents by viewModel.actualParents.observeAsState(emptyList())
            if (actualParents.size > 1) {
                // Row for title
                Row {
                    Text(actualParents.first().name)
                }

                Icon(Icons.Default.MoreVert, contentDescription = "...")


                var viewParents by remember { mutableStateOf(false) }
                IconButton(onClick = { viewParents = !viewParents }) {
                    if (viewParents)
                        Icon(Icons.Default.ArrowDropUp, contentDescription = "hide parents")
                    else
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "view parents")
                }
                if (viewParents) {
                    actualParents.subList(1, actualParents.size).forEach {
                        Text(it.name)
                    }
                }

                Icon(Icons.Default.MoreVert, contentDescription = "...")
            }

            TaskPlayer(viewModel)
        } else {
            Text("Loading...")
        }
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
                    Icon(Icons.Default.SkipPrevious, contentDescription = "undo completion")
                }


                val onClickAction: () -> Unit = if (viewModel.isStarted) {
                    { viewModel.completeExecution() }
                } else {
                    { viewModel.startActualExecution() }
                }
                val icon = if (viewModel.isStarted) Icons.Default.Done else Icons.Default.PlayArrow
                val contentDescription = if (viewModel.isStarted) "complete" else "start"

                IconButton(onClick = onClickAction) {
                    Icon(icon, contentDescription = contentDescription)
                }

                var showDialog by remember { mutableStateOf(false) }
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "skip")
                }
                if (showDialog) {
                    SkipDialog(
                        onSkip = {
                            viewModel.skipCurrentExecution(it)
                            showDialog = false
                        },
                        onDismissRequest = { showDialog = false }
                    )
                }
            }
        }
        if (viewModel.state.nextTask != null) {
            Text("Next: ${viewModel.state.nextTask!!.name}")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkipDialog(
    onSkip: (EndReason) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Skip reason", style = MaterialTheme.typography.displaySmall)

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EndReason.entries.forEach { reason ->
                        Button(onClick = { onSkip(reason) }) {
                            Text(reason.name)
                        }
                    }
                }
            }
        }
    }
}