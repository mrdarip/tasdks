package com.mrdarip.tasdks.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.RepetitionRange
import com.mrdarip.tasdks.data.entity.RepetitionType
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreens
import com.mrdarip.tasdks.screens.viewModels.MainMenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TaskCard(task: Task, placeName: String, onClick: () -> Unit = {}) {
    Box(modifier = Modifier) {

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
                text = task.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                text = task.comment ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = placeName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

        }
        Text(
            text = task.iconEmoji ?: "🗒️",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.absoluteOffset(12.dp, (-14).dp)
        )
    }

}

@Composable
fun TasksCardRow(
    tasks: List<Task>,
    title: String,
    mainMenuViewModel: MainMenuViewModel,
    navController: NavController
) {
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
        items(tasks) { task ->
            val placeName by mainMenuViewModel.getPlaceName(task.placeId)
                .collectAsState(initial = "")
            TaskCard(
                task = task,
                placeName = placeName,
                onClick = {
                    mainMenuViewModel.viewModelScope.launch(Dispatchers.IO) {
                        val activatorId = mainMenuViewModel.insertActivator(
                            Activator(
                                comment = "created for one time execution",
                                repetitionRange = RepetitionRange(
                                    minRepSec = 0,
                                    optRepSec = 0,
                                    maxRepSec = 0,
                                    repetitionType = RepetitionType.DATE
                                ),
                                endAfterDate = null,
                                endAfterRep = 1,
                                userCancelled = false,
                                taskToActivateId = task.taskId
                            )
                        )
                        Log.d("FYI", "Activator id: $activatorId")
                        withContext(Dispatchers.Main) {
                            navController.navigate("${AppScreens.PlayActivator.route}/$activatorId")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TaskLiItem(task: Task, placeName: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {// TODO: Add task min/opt/max
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                task.iconEmoji ?: "🤕",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Column {
                Text(
                    text = task.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = task.comment ?: "NO DESCRIPTION",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = placeName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskLiItemPreview() {
    TaskLiItem(
        task = Task(
            name = "Task name",
            comment = "Task comment",
            iconEmoji = "🐱",
            placeId = null
        ),
        placeName = "Place name"
    )
}

@Composable
fun TasksRow(
    tasks: List<Task>,
    navController: NavController,
    showEditButton: Boolean,
    onClickEdit: () -> Unit = {}
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (showEditButton) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onClickEdit() })
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit tasks order")
                        Column {
                            Text(
                                text = "Edit",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }
        }
        items(tasks) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { navController.navigate(AppScreens.EditTask.route + "/${task.taskId}") })
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        task.iconEmoji ?: "🤕",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Column {
                        Text(
                            text = task.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectTaskRow(
    tasks: List<Task>,
    onTaskClicked: (Task) -> Unit
) {//TODO: review this function, adding a searchbar
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(tasks) { task ->
            TaskCard(task = task, placeName = "", onClick = { onTaskClicked(task) })
        }
    }
}

@Composable
fun OrderTaskLiItem(
    task: Task,
    position: Long,
    maxPosition: Long,
    onMoveUpClicked: () -> Unit,
    onMoveDownClicked: () -> Unit,
    onXclicked: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                task.iconEmoji ?: "🤕",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onMoveUpClicked()
                }, enabled = position > 0) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Move task order up"
                    )
                }
                Text(
                    text = task.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        onXclicked()
                    },

                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Remove Task"
                    )
                }

                IconButton(
                    onClick = {
                        onMoveDownClicked()
                    },
                    enabled = position < maxPosition - 1
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Move task order down"
                    )
                }
            }
        }
    }
}