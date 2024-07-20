package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.ActivatorFields
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditActivatorViewModel

@Composable
fun EditActivatorScreen(navController: NavController, activatorId: Long) {
    val mainMenuViewModel = viewModel(modelClass = EditActivatorViewModel::class.java)
    var activator by remember { mutableStateOf(Activator(taskToActivateId = -1)) }

    LaunchedEffect(mainMenuViewModel) {
        mainMenuViewModel.getActivatorById(activatorId).collect { fetchedActivator ->
            activator = fetchedActivator
        }
    }

    EditActivatorBodyContent(
        navController = navController,
        viewModel = mainMenuViewModel,
        activator = activator,
        onActivatorChanged = { activator = it }
    )
}

@Composable
fun EditActivatorBodyContent(
    navController: NavController,
    viewModel: EditActivatorViewModel,
    activator: Activator,
    onActivatorChanged: (Activator) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Edit activator", style = MaterialTheme.typography.headlineLarge)

        ActivatorFields(
            activator = activator,
            possibleTasksToActivate = viewModel.state.tasks,
            onActivatorChanged = onActivatorChanged
        )

        Button(onClick = {
            viewModel.upsertActivator(activator)
            navController.popBackStack()
        }, modifier = Modifier.padding(horizontal = 16.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = "Localized description")
            Text("Save")
        }
    }
}