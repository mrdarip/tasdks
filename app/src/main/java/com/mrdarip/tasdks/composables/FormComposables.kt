package com.mrdarip.tasdks.composables

import android.icu.text.BreakIterator
import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.RepetitionUnit
import com.mrdarip.tasdks.data.entity.Task
import java.util.Date
import java.util.Locale


@Composable
@Preview
fun TaskFields(
    task: Task = Task(),
    onTaskChange: (Task) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = task.name,
                onValueChange = { onTaskChange(task.copy(name = it)) },
                label = { Text("Name") },
                placeholder = { Text("Task name") },
                modifier = Modifier.weight(3f),
                singleLine = true
            )
            TextField(
                value = task.iconEmoji.orEmpty(),
                onValueChange = { onTaskChange(task.copy(iconEmoji = it.ifBlank { null })) },
                label = { Text("Emoji") },
                placeholder = { Text("😃") },
                modifier = Modifier.weight(1f),
                isError = !isValidEmoji(task.iconEmoji.orEmpty())
            )
        }
        TextField(
            value = task.comment.orEmpty(),
            onValueChange = { onTaskChange(task.copy(comment = it.ifBlank { null })) },
            label = { Text("Comment") },
            placeholder = { Text("Task comment") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "is playlist")
            Checkbox(
                checked = task.isPlaylist,
                onCheckedChange = { onTaskChange(task.copy(isPlaylist = it)) }
            )
        }
        if (!task.isPlaylist) {
            TextField(
                value = task.waitTime.toString(),
                onValueChange = { onTaskChange(task.copy(waitTime = it.toIntOrNull() ?: 0)) },
                label = { Text("Wait time") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "allow running parallel tasks")
                Checkbox(
                    checked = task.allowParallelTasks,
                    onCheckedChange = { onTaskChange(task.copy(allowParallelTasks = it)) }
                )
            }
        }
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


@Composable
fun ActivatorFields(
    activator: Activator = Activator(taskToActivateId = -1),
    possibleTasksToActivate: List<Task> = listOf(Task(), Task(), Task(), Task(), Task()),
    onActivatorChanged: (Activator) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StartDateInput(activator, onActivatorChanged)
        HorizontalDivider()

        RepetitionTypeInput(activator, onActivatorChanged)
        RepetitionUnitInput(activator, onActivatorChanged)
        RepetitionsRangeInput(activator, onActivatorChanged)
        HorizontalDivider()

        EndAfterFactorInput(activator, onActivatorChanged)
        HorizontalDivider()

        SelectActivatedTaskInput(possibleTasksToActivate, activator, onActivatorChanged)
        HorizontalDivider()

        ActivatorDescriptionInput(activator, onActivatorChanged)
    }
}

@Composable
private fun ActivatorDescriptionInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepetitionTypeInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    var selectedIndex = if (activator.repetitionRange.exactDateRange) 0 else 1
    val options = listOf("Date", "Time")
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        selectedIndex = index
                        onActivatorChanged(
                            activator.copy(
                                repetitionRange = activator.repetitionRange.copy(
                                    exactDateRange = (index == 0)
                                )
                            )
                        )
                    },
                    selected = index == selectedIndex
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun RepetitionUnitInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(RepetitionUnit.entries) { option ->
            val selected = activator.repetitionRange.repetitionUnit == option
            FilterChip(
                onClick = {
                    onActivatorChanged(
                        activator.copy(
                            repetitionRange = activator.repetitionRange.copy(
                                repetitionUnit = option
                            )
                        )
                    )
                },
                label = {
                    Text(capitalized(option.name))
                },
                selected = selected,
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
            )
        }

    }
}

@Composable
private fun SelectActivatedTaskInput(
    possibleTasksToActivate: List<Task>,
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    LazyHorizontalGrid(
        modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        rows = GridCells.Adaptive(64.dp), contentPadding = PaddingValues(horizontal = 16.dp)
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
            SelectableGridTask(
                task = task,
                placeName = "changeme",
                selected = (task.taskId == activator.taskToActivateId),
                onClick = {
                    onActivatorChanged(activator.copy(taskToActivateId = task.taskId))
                })
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EndAfterFactorInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = "Kill after repetitions")
        TextField(
            value = activator.endRep?.toString() ?: "",
            label = { Text("End after repetitions") },
            onValueChange = { onActivatorChanged(activator.copy(endRep = it.toIntOrNull())) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        val openSelectEndAfterDateDialog = remember { mutableStateOf(false) }

        Text(text = "Kill after date")
        Button(
            onClick = { openSelectEndAfterDateDialog.value = true },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (activator.endDate == null || activator.endDate < 0) "Select End Date"
                else DateFormat.format(
                    "dd/MM/yyyy",
                    Date(activator.endDate.toLong() * 1000)
                ).toString()
            )
        }

        if (openSelectEndAfterDateDialog.value) {
            val datePickerState = rememberDatePickerState(activator.endDate?.times(1000L))
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
                            onActivatorChanged(activator.copy(endDate = (datePickerState.selectedDateMillis!! / 1000).toInt()))
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
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun StartDateInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("START:")
        Button(
            onClick = { openDialog.value = true }
        ) {
            Text(
                text = if (activator.repetitionRange.firstTimeDone < 0) "Select Start Date"
                else DateFormat.format(
                    "dd/MM/yyyy",
                    Date(activator.repetitionRange.firstTimeDone.toLong() * 1000)
                ).toString()
            )
        }
    }


    if (openDialog.value) {
        val datePickerState =
            rememberDatePickerState(if (activator.repetitionRange.firstTimeDone < 0) null else activator.repetitionRange.start * 1000L)
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
                                    firstTimeDone = ((datePickerState.selectedDateMillis!! / 1000).toInt())
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
}

@Composable
private fun RepetitionsRangeInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row {
            Text(text = "min", modifier = Modifier.weight(1f))

            TextField(
                value = activator.repetitionRange.start.toString(),
                onValueChange = {
                    onActivatorChanged(
                        activator.copy(
                            repetitionRange = activator.repetitionRange.copy(
                                start = it.toIntOrNull() ?: 0
                            )
                        )
                    )
                },
                label = { Text(capitalized(activator.repetitionRange.repetitionUnit.name) + " until start") },
                placeholder = { Text("Activator Description") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        Row {
            Text(text = "max", modifier = Modifier.weight(1f))
            TextField(
                value = activator.repetitionRange.end.toString(),
                onValueChange = {
                    onActivatorChanged(
                        activator.copy(
                            repetitionRange = activator.repetitionRange.copy(
                                end = it.toIntOrNull() ?: 0
                            )
                        )
                    )
                },
                label = { Text(capitalized(activator.repetitionRange.repetitionUnit.name) + " until deadline") },
                placeholder = { Text("Activator Description") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}

fun capitalized(text: String): String {
    return text.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }
}