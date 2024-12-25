package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TwoButtonsListItem
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreen
import com.mrdarip.tasdks.screens.managementScreens.viewModels.ManageActivatorsViewModel


@Composable
fun ManageActivatorsScreen(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = ManageActivatorsViewModel::class.java)
    ManageActivatorsBodyContent(
        navController = navController,
        viewModel = mainMenuViewModel,
    )
}

@Composable
private fun ManageActivatorsBodyContent(
    navController: NavController,
    viewModel: ManageActivatorsViewModel
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp, 0.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { //TODO: make it work when clicking the card, not the icon
            TwoButtonsListItem(
                title = "Create new", onPrimaryClick = {
                    navController.navigate("${AppScreen.CreateActivator.route}/-1")
                },
                primaryIcon = Icons.Filled.Add,
                surfaceColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        items(viewModel.state.activeActivators) { activator ->
            val taskToActivate = viewModel.getTaskById(activator.taskToActivateId)
                .collectAsState(
                    initial = Task(
                        name = "...",
                        comment = null,
                        iconEmoji = null
                    )
                ).value
            TwoButtonsListItem(
                title = taskToActivate.name,
                subTitle = activator.comment ?: "No comment provided",
                emoji = taskToActivate.iconEmoji,
                onPrimaryClick = {
                    navController.navigate("${AppScreen.PlayExecution.route}/activator/${activator.activatorId}")
                },
                primaryIcon = Icons.Filled.PlayArrow,
                onSecondaryClick = {
                    navController.navigate("${AppScreen.EditActivator.route}/${activator.activatorId}")
                },
                secondaryIcon = Icons.Filled.Edit,
                onLiItemClick = {
                    //TODO: Implement navigating to activator details
                },
                surfaceColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}