package com.mrdarip.tasdks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.data.entity.Task

@Preview
@Composable
fun TaskFields(
    taskName: String = "test",
    taskEmoji: String = "🍀",
    taskComment: String = "comment test",
    onTaskNameChange: (String) -> Unit = {},
    onTaskEmojiChange: (String) -> Unit = {},
    onTaskCommentChange: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = taskName,
                onValueChange = { onTaskNameChange(it) },
                label = { Text("Name") },
                placeholder = { Text("Task name") },
                modifier = Modifier.weight(3f),
                singleLine = true
            )
            TextField(
                value = taskEmoji,
                onValueChange = { onTaskEmojiChange(it) },
                label = { Text("Emoji") },
                placeholder = { Text("😃") },
                modifier = Modifier.weight(1f)
            )
        }
        TextField(
            value = taskComment,
            onValueChange = { onTaskCommentChange(it) },
            label = { Text("Comment") },
            placeholder = { Text("Task comment") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun ActivatorFields(
    activatorComment: String = "test",
    possibleTasksToActivate: List<Task> = listOf(Task(0, "hey", "", null, null),Task(0, "hey", "", null, null),Task(0, "hey", "", null, null),Task(0, "hey", "", null, null),Task(0, "hey", "", null, null),Task(0, "hey", "", null, null),Task(0, "hey", "", null, null)),
    taskToActivate: Task? = Task(0, "hey", "", null, null),
    onCommentChange: (String) -> Unit = {},
    onTaskToActivateChange: (Task) -> Unit = {},
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextField(
            value = activatorComment,
            onValueChange = { onCommentChange(it) },
            label = { Text("Description") },
            placeholder = { Text("Activator Description") },
            modifier = Modifier.fillMaxWidth()
        )

        LazyHorizontalGrid(
            modifier = Modifier
                .padding(8.dp, 8.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            rows = GridCells.Adaptive(128.dp),

        ) {
            item{
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.width(150.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable(onClick = { /*TODO*/ })
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🔎 SEARCH",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }//TODO implement search. Card based on TaskCard

            items(possibleTasksToActivate) { task ->
                TaskCard(task = task, placeName = "changeme", onClick = {
                    onTaskToActivateChange(task)
                })
            }
        }
    }
}

