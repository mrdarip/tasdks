package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
    var activator = viewModel.getActivatorById(activatorId).collectAsState(
        initial = Activator(
            taskToActivateId = -1
        )
    ).value

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        ActivatorFields(
            activator = activator,
            possibleTasksToActivate = viewModel.state.tasks,
            onActivatorChanged = { activator = it }
        )


        Button(onClick = {
            viewModel.upsertActivator(activator)
            navController.popBackStack()
        }) {
            Icon(Icons.Filled.Edit, contentDescription = "Localized description")
            Text("Save")
        }
    }
}