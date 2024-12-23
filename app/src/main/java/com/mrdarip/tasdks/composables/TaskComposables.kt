package com.mrdarip.tasdks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreen
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview
@Composable
fun TaskCard(task: Task = Task(), onClick: () -> Unit = {}) {
    TasdksCard(
        emoji = task.iconEmoji,
        title = task.name,
        subTitle = task.comment,
        onClick = onClick
    )
}

@Preview
@Composable
fun SelectableGridTask(
    task: Task = Task(),
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box {
        Column(
            verticalArrangement = Arrangement.Top, modifier = Modifier
                .width(150.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .then(
                    if (selected) Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) else Modifier
                )
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
            if (task.comment != null) {
                Text(
                    text = task.comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
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
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onClick = {
                        mainMenuViewModel.viewModelScope.launch(Dispatchers.IO) {
                            val activatorId = mainMenuViewModel.insertActivator(
                                Activator(
                                    comment = "created for one time execution",
                                    taskToActivateId = task.taskId,
                                    endRep = 1
                                ) //TODO: check RepetitionRange for one time repetition
                            )

                            withContext(Dispatchers.Main) {
                                navController.navigate("${AppScreen.PlayActivator.route}/$activatorId")
                            }
                        }
                    }
                )
            }
        }
    }
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
                    .clickable(onClick = { navController.navigate(AppScreen.EditTask.route + "/${task.taskId}") })
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
            TaskCard(task = task, onClick = { onTaskClicked(task) })
        }
    }
}

@Composable
fun SelectTaskGrid(
    tasks: List<Task>,
    onTaskClicked: (Task) -> Unit
) {//TODO: review this, adding a searchbar, fix padding...
    LazyVerticalGrid(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(tasks) { task ->
            TaskCard(task = task, onClick = { onTaskClicked(task) })
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