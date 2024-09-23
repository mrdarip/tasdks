package com.mrdarip.tasdks.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreen
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuViewModel

@Preview
@Composable
fun MiniActivatorPlayer(
    activator: Activator = Activator(taskToActivateId = 1),
    topTask: Task = Task(name = "Top Task"),
    currentTask: Task = Task(name = "Current Task"),
    onPrimaryClick: () -> Unit = {},
    onSecondaryClick: () -> Unit = {}
) {
    TwoButtonsListItem(
        title = currentTask.name,
        subTitle = "From: ${topTask.name}",
        emoji = currentTask.iconEmoji,
        primaryIcon = Icons.Filled.PlayArrow,
        secondaryIcon = Icons.Filled.Done,
        onPrimaryClick = onPrimaryClick,
        onSecondaryClick = onSecondaryClick,
        onLiItemClick = {
            //TODO: Implement navigating to full screen play activator screen
        }
    )
}


@Composable
fun ActivatorCardRow(
    tasks: List<Activator>,
    title: String,
    mainMenuViewModel: MainMenuViewModel,
    navController: NavController
) {
    if (tasks.isNotEmpty()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(16.dp, 32.dp, 16.dp, 8.dp)
                .fillMaxWidth()
        )

        LazyRow(
            modifier = Modifier.padding(0.dp, 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(tasks) { activator ->
                val task = mainMenuViewModel.getTaskById(activator.taskToActivateId)
                    .collectAsState(initial = Task()).value

                val taskETA = mainMenuViewModel.getTaskETA(activator.taskToActivateId)
                    .collectAsState(initial = 0).value

                TasdksCard(
                    emoji = task.iconEmoji,
                    title = task.name,
                    subTitle = "ETA: $taskETA min\n" + activator.comment,
                    onClick = {
                        navController.navigate("${AppScreen.PlayActivator.route}/${activator.activatorId}")
                    }
                )
            }
        }
    }
}