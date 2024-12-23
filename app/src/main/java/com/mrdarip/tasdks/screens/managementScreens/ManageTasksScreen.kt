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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TwoButtonsListItem
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.navigation.AppScreen
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuState
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ManageTasksScreen(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    ManageTasksBodyContent(
        navController = navController,
        mainMenuViewModel = mainMenuViewModel,
        mainMenuState = mainMenuState
    )
}

@Composable
private fun ManageTasksBodyContent(
    navController: NavController,
    mainMenuViewModel: MainMenuViewModel,
    mainMenuState: MainMenuState
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp, 0.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { //TODO: make it work when clicking the card, not the icon
            TwoButtonsListItem(
                "New task", onPrimaryClick = {
                    navController.navigate(AppScreen.CreateTask.route)
                },
                primaryIcon = Icons.Filled.Add,
                surfaceColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
        items(mainMenuState.activeTasks) { task ->
            TwoButtonsListItem(
                title = task.name,
                subTitle = task.comment ?: "No comment",
                emoji = task.iconEmoji,
                primaryIcon = Icons.Filled.PlayArrow,
                secondaryIcon = Icons.Filled.Edit,
                onPrimaryClick = {
                    mainMenuViewModel.viewModelScope.launch(Dispatchers.IO) {
                        val activatorId = mainMenuViewModel.insertActivator(
                            Activator(
                                comment = "Created for one time execution",
                                taskToActivateId = task.taskId,
                                endAfterRepetitions = 1
                            )
                        )

                        withContext(Dispatchers.Main) {
                            navController.navigate("${AppScreen.PlayActivator.route}/activator/$activatorId")
                        }
                    }
                },
                onSecondaryClick = {
                    navController.navigate(
                        "${AppScreen.EditTask.route}/${task.taskId}"
                    )
                },
                onLiItemClick = {
                    //TODO: Implement navigating to task details
                },
                surfaceColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

