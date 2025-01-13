package com.mrdarip.tasdks.composables.forms

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.composables.SelectableGridTask
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.RepetitionUnit
import com.mrdarip.tasdks.data.entity.Task
import java.time.Instant
import java.util.Date

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
        ActivatorDescriptionInput(activator, onActivatorChanged)
        StartDateInput(activator, onActivatorChanged)
        HorizontalDivider()

        RepetitionUnitInput(activator, onActivatorChanged)
        RepetitionsRangeInput(activator, onActivatorChanged)
        HorizontalDivider()

        EndAfterFactorInput(activator, onActivatorChanged)
        HorizontalDivider()

        SelectActivatedTaskInput(possibleTasksToActivate, activator, onActivatorChanged)
    }
}

@Composable
private fun ActivatorDescriptionInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    TextInput(
        value = activator.comment ?: "",
        onValueChange = { onActivatorChanged(activator.copy(comment = it)) },
        label = "Description",
        placeholder = "Activator Description",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
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
                leadingIcon = {
                    if (selected) {

                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )

                    } else {
                        Icon(
                            imageVector =
                            if (option.isExactDate)
                                Icons.Filled.DateRange
                            else
                                Icons.Filled.Home,
                            contentDescription = "Done icon",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
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
    if (possibleTasksToActivate.isEmpty()) {
        return
    }

    LazyHorizontalGrid(
        modifier = Modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        rows = GridCells.Adaptive(64.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
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
                selected = (task.taskId == activator.taskToActivateId),
                onClick = {
                    onActivatorChanged(activator.copy(taskToActivateId = task.taskId))
                })
        }
    }
}

@Composable
private fun EndAfterFactorInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Kill after repetitions")
        TextField(
            value = activator.endAfterRepetitions?.toString() ?: "",
            label = { Text("End after repetitions") },
            onValueChange = { onActivatorChanged(activator.copy(endAfterRepetitions = it.toIntOrNull())) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Kill after date")
        DateInput(
            date = activator.endAfterDate ?: Instant.MIN,
            onDateChanged = {
                onActivatorChanged(
                    activator.copy(
                        endAfterDate = if (it.isBefore(
                                Instant.EPOCH
                            )
                        ) null else it
                    )
                )
            })
    }
}

@Composable
private fun StartDateInput(
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("START:")

        DateInput(activator.repetitionRange.firstTimeDone, onDateChanged = {
            onActivatorChanged(
                activator.copy(repetitionRange = activator.repetitionRange.copy(firstTimeDone = it))
            )
        })
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DateInput(
    date: Instant,
    onDateChanged: (Instant) -> Unit
) {
    val openDialog = remember { mutableStateOf(false) }

    Button(
        onClick = { openDialog.value = true }
    ) {
        Text(
            text = if (date.isBefore(Instant.EPOCH)) "Select Date"
            else DateFormat.format(
                "dd/MM/yyyy",
                Date(date.epochSecond)
            ).toString()
        )
    }

    if (openDialog.value) {
        val datePickerState =
            rememberDatePickerState(if (date.isBefore(Instant.EPOCH)) null else date.epochSecond)
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
                        onDateChanged(
                            Instant.ofEpochSecond(datePickerState.selectedDateMillis ?: 0)
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (activator.repetitionRange.repetitionUnit.isExactDate) {
                DateInput(
                    date = Instant.ofEpochSecond(activator.repetitionRange.start),
                    onDateChanged = {
                        onActivatorChanged(
                            activator.copy(
                                repetitionRange = activator.repetitionRange.copy(start = it.epochSecond)
                            )
                        )
                    })
            } else {
                NumberInput(
                    value = activator.repetitionRange.start.toInt(),
                    onValidValueChange = {
                        onActivatorChanged(
                            activator.copy(
                                repetitionRange = activator.repetitionRange.copy(
                                    start = it.toLong()
                                )
                            )
                        )
                    },
                    label = capitalized(activator.repetitionRange.repetitionUnit.name) + " until start",
                    placeholder = "Activator Description",
                    modifier = Modifier.weight(1f)
                )
            }

            if (activator.repetitionRange.repetitionUnit.isExactDate) {
                DateInput(
                    date = Instant.ofEpochSecond(activator.repetitionRange.end.toLong()),
                    onDateChanged = {
                        onActivatorChanged(
                            activator.copy(
                                repetitionRange = activator.repetitionRange.copy(end = it.epochSecond.toInt())
                            )
                        )
                    })
            } else {
                NumberInput(
                    value = activator.repetitionRange.end,
                    onValidValueChange = {
                        onActivatorChanged(
                            activator.copy(
                                repetitionRange = activator.repetitionRange.copy(
                                    end = it
                                )
                            )
                        )
                    },
                    label = capitalized(activator.repetitionRange.repetitionUnit.name) + " until deadline",
                    placeholder = "Activator Description",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}