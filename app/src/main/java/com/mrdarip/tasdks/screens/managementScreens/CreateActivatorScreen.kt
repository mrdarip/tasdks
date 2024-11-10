package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.forms.ActivatorFields
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.RepetitionRange
import com.mrdarip.tasdks.screens.managementScreens.viewModels.CreateActivatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateActivatorScreen(navController: NavController, preSelectedTaskId: Long) {
    val createActivatorViewModel = viewModel(modelClass = CreateActivatorViewModel::class.java)
    CreateActivatorBodyContent(
        navController = navController,
        createActivatorViewModel = createActivatorViewModel,
        preSelectedTaskId = preSelectedTaskId
    )
}

@Composable
private fun CreateActivatorBodyContent(
    navController: NavController,
    createActivatorViewModel: CreateActivatorViewModel,
    preSelectedTaskId: Long
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        var newActivator by remember {
            mutableStateOf(
                Activator(
                    comment = null,
                    taskToActivateId = preSelectedTaskId,
                    endAfterDate = null,
                    userCancelled = false,
                    repetitionRange = RepetitionRange(),
                    endAfterRepetitions = null
                )
            )
        }
        Text(text = "Create activator", style = MaterialTheme.typography.headlineLarge)

        ActivatorFields(
            activator = newActivator,
            possibleTasksToActivate = if (preSelectedTaskId == -1L) createActivatorViewModel.state.tasks else emptyList(),
            onActivatorChanged = { newActivator = it }
        )

        Button(
            onClick = {
                //TODO: Tell user why it can't submit
                //TODO: Move check to ActivatorFields
                if (newActivator.taskToActivateId != -1L) {//if task to activate is -1 it means that no task was selected
                    createActivatorViewModel.viewModelScope.launch(Dispatchers.IO) {
                        createActivatorViewModel.insertActivator(newActivator)
                        withContext(Dispatchers.Main) {
                            navController.popBackStack()//todo: should this be moved outside the coroutine?
                        }
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task")
            Text("Add")
        }
    }

}