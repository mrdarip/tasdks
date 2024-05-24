package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.ActivatorFields
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditActivatorViewModel

@Composable
fun EditActivatorScreen(navController: NavController, activatorId: Long) {
    val mainMenuViewModel = viewModel(modelClass = EditActivatorViewModel::class.java)
    EditActivatorBodyContent(
        navController = navController,
        viewModel = mainMenuViewModel,
        activatorId = activatorId
    )
}

@Composable
fun EditActivatorBodyContent(
    navController: NavController,
    viewModel: EditActivatorViewModel,
    activatorId: Long
) {
    val activator by viewModel.getActivatorById(activatorId).collectAsState(
        initial = Activator(
            taskToActivateId = -1
        )
    )

    var repetitionRange by rememberSaveable { mutableStateOf(activator.repetitionRange) }
    var comment by rememberSaveable { mutableStateOf("") }
    var taskToActivateId by rememberSaveable { mutableLongStateOf(0L) }

    LaunchedEffect(activator) {
        comment = activator.comment ?: ""
        repetitionRange = activator.repetitionRange
        taskToActivateId = activator.taskToActivateId
    }

    Column {
        ActivatorFields(
            activator = activator,
            possibleTasksToActivate = viewModel.state.tasks,
            onCommentChange = { comment = it },
            onTaskToActivateChange = { taskToActivateId = it.taskId },
            onMaxRepChange = { repetitionRange = repetitionRange.copy(maxRep = it.toInt()) },
            onMinRepChange = { repetitionRange = repetitionRange.copy(minRep = it) },
            onOptRepChange = { repetitionRange = repetitionRange.copy(optRep = it) },
            onStartDateChange = { repetitionRange = repetitionRange.copy(startDate = it) }
        )


        Button(onClick = {
            viewModel.upsertActivator(
                activator.copy(
                    comment = comment,
                    taskToActivateId = taskToActivateId
                )//todo: use copy in other cases
            )
            navController.popBackStack()
        }) {
            Icon(Icons.Filled.Edit, contentDescription = "Localized description")
            Text("Save")
        }
    }
}