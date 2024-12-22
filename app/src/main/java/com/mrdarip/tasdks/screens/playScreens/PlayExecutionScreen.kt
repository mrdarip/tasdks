package com.mrdarip.tasdks.screens.playScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TasdksCard

@Composable
fun PlayExecutionScreen(executionId: Long, navController: NavController) {
    val playExecutionViewModel = viewModel(modelClass = PlayExecutionViewModel::class.java)
    playExecutionViewModel.setExecution(executionId)
    PlayActivatorBodyContent(
        viewModel = playExecutionViewModel
    )
}

@Composable
private fun PlayActivatorBodyContent(
    viewModel: PlayExecutionViewModel
) {
    Column {
        Row {
            Text("Row for actions")
        }

        val parentExecution = viewModel.getCurrentExecutionParent()
        if (parentExecution != null) {
            TasdksCard(
                title = "parent: ${parentExecution.executionId}",
            )
        }
        Row {
            viewModel.getCurrentExecutionBrothers().forEachIndexed { index, execution ->
                TasdksCard(
                    title = "execution: ${execution.name}, level $index"
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {

            IconButton(onClick = { }) {
                Icon(Icons.Default.Share, contentDescription = "go back")
            }
            IconButton(onClick = { }) {
                if (viewModel.isRunning()) {
                    Icon(Icons.Default.Done, contentDescription = "complete")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "start")
                }
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "skip")
            }
        }
    }
}