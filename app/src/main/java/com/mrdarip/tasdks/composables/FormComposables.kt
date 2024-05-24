package com.mrdarip.tasdks.composables

import android.icu.text.BreakIterator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.data.entity.Activator
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
    activator: Activator = Activator(taskToActivateId = -1),
    possibleTasksToActivate: List<Task> = listOf(Task(), Task(), Task(), Task(), Task()),
    onActivatorChanged: (Activator) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = activator.comment ?: "",
            onValueChange = { onActivatorChanged(activator.copy(comment = it)) },
            label = { Text("Description") },
            placeholder = { Text("Activator Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = if (activator.repetitionRange.minRep == null) "" else activator.repetitionRange.minRep.toString(),
                onValueChange = {
                    onActivatorChanged(
                        activator.copy(
                            repetitionRange = activator.repetitionRange.copy(
                                minRep = it.toIntOrNull()
                            )
                        )
                    )
                },
                label = { Text("Min Repetitions") },
                placeholder = { Text("Activator Description") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = activator.repetitionRange.optRep?.toString() ?: "",
                onValueChange = {
                    onActivatorChanged(
                        activator.copy(
                            repetitionRange = activator.repetitionRange.copy(
                                optRep = it.toIntOrNull()
                            )
                        )
                    )
                },
                label = { Text("Opt Repetitions") },
                placeholder = { Text("Activator Description") },

                modifier = Modifier.weight(1f)
            )
            TextField(
                value = activator.repetitionRange.maxRep?.toString() ?: "",
                onValueChange = {
                    onActivatorChanged(
                        activator.copy(
                            repetitionRange = activator.repetitionRange.copy(
                                maxRep = it.toIntOrNull()
                            )
                        )
                    )
                },
                label = { Text("Max Repetitions") },
                placeholder = { Text("Activator Description") },
                modifier = Modifier.weight(1f)
            )
        }
        val openDialog = remember { mutableStateOf(false) }
        Button(
            onClick = { openDialog.value = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Select Start Date")
        }

        if (openDialog.value) {
            val datePickerState = rememberDatePickerState()
            val confirmEnabled = remember {
                derivedStateOf { datePickerState.selectedDateMillis != null }
            }
            DatePickerDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            onActivatorChanged(
                                activator.copy(
                                    repetitionRange = activator.repetitionRange.copy(
                                        startDate = ((datePickerState.selectedDateMillis!! / 1000).toInt())
                                    )
                                )
                            )
                        },
                        enabled = confirmEnabled.value
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Text("END", modifier = Modifier.padding(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            val openSelectEndAfterDateDialog = remember { mutableStateOf(false) }
            Button(
                onClick = { openSelectEndAfterDateDialog.value = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Select End Date")
            }

            if (openSelectEndAfterDateDialog.value) {
                val datePickerState = rememberDatePickerState()
                val confirmEnabled = remember {
                    derivedStateOf { datePickerState.selectedDateMillis != null }
                }
                DatePickerDialog(
                    onDismissRequest = {
                        openSelectEndAfterDateDialog.value = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openSelectEndAfterDateDialog.value = false
                                onActivatorChanged(activator.copy(endAfterDate = (datePickerState.selectedDateMillis!! / 1000).toInt()))
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openSelectEndAfterDateDialog.value = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            TextField(
                value = "repetitions",
                onValueChange = { onActivatorChanged(activator.copy(endAfterRep = it.toIntOrNull())) },
                modifier = Modifier.weight(1f)
            )
        }



        LazyHorizontalGrid(
            modifier = Modifier.height(200.dp),
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
                    onActivatorChanged(activator.copy(taskToActivateId = task.taskId))
                })
            }
        }
    }
}

