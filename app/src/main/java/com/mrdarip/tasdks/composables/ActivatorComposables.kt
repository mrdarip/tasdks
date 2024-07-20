package com.mrdarip.tasdks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreens
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
                ActivatorCard(
                    activator = activator,
                    taskName = mainMenuViewModel.getTaskById(activator.taskToActivateId)
                        .collectAsState(initial = Task()).value.name,
                    onClick = {
                        navController.navigate("${AppScreens.PlayActivator.route}/${activator.activatorId}")
                    }
                )
            }
        }
    }
}

@Composable
fun ActivatorCard(activator: Activator, taskName: String, onClick: () -> Unit = {}) {

    Column(
        verticalArrangement = Arrangement.Top, modifier = Modifier
            .width(150.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = taskName,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        Text(
            text = activator.comment ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3
        )
    }

}