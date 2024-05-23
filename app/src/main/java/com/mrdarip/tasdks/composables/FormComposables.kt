package com.mrdarip.tasdks.composables

import android.icu.text.BreakIterator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.data.entity.RepetitionRange
import com.mrdarip.tasdks.data.entity.Task


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
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                modifier = Modifier.weight(1f),
                isError = isValidEmoji(taskEmoji)
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

fun isValidEmoji(emoji: String): Boolean {
    return getLength(emoji) <= 1
}

fun getLength(emoji: String?): Int {
    val it: BreakIterator = BreakIterator.getCharacterInstance()
    it.setText(emoji)
    var count = 0
    while (it.next() != BreakIterator.DONE) {
        count++
    }
    return count
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ActivatorFields(
    activatorComment: String = "test",
    repetition: RepetitionRange = RepetitionRange(),
    possibleTasksToActivate: List<Task> = listOf(Task(), Task(), Task(), Task(), Task()),
    taskToActivate: Task? = Task(0, "hey", "", null, null),
    onCommentChange: (String) -> Unit = {},
    onMinRepChange: (Int?) -> Unit = {},
    onOptRepChange: (Int?) -> Unit = {},
    onMaxRepChange: (String) -> Unit = {},
    onTaskToActivateChange: (Task) -> Unit = {},
) {
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayMode = DisplayMode.Input
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = activatorComment,
            onValueChange = { onCommentChange(it) },
            label = { Text("Description") },
            placeholder = { Text("Activator Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = if (repetition.minRep == null) "" else repetition.minRep.toString(),
                onValueChange = { onMinRepChange(it.toIntOrNull()) },
                label = { Text("Min Repetitions") },
                placeholder = { Text("Activator Description") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = repetition.optRep?.toString() ?: "",
                onValueChange = { onOptRepChange(it.toIntOrNull()) },
                label = { Text("Opt Repetitions") },
                placeholder = { Text("Activator Description") },

                modifier = Modifier.weight(1f)
            )
            TextField(
                value = repetition.maxRep?.toString() ?: "",
                onValueChange = { onMaxRepChange(it) },
                label = { Text("Max Repetitions") },
                placeholder = { Text("Activator Description") },
                modifier = Modifier.weight(1f)
            )
        }
        DatePicker(state = dateState, showModeToggle = true)

        LazyHorizontalGrid(
            modifier = Modifier.height(200.dp), // This will make the LazyHorizontalGrid fill the remaining height
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            rows = GridCells.Adaptive(128.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .width(150.dp)
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
                TaskCardGrid(task = task, placeName = "changeme", onClick = {
                    onTaskToActivateChange(task)
                })
            }
        }
    }
}

