package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TwoButtonsListItem
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreens
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
fun ManageActivatorsBodyContent(
    navController: NavController,
    viewModel: ManageActivatorsViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(0.dp, 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(viewModel.state.activeActivators) { activator ->
                val taskToActivate = viewModel.getTaskById(activator.taskToActivateId).collectAsState(initial = Task(name = "...", comment = null, iconEmoji = null, placeId = null)).value
                TwoButtonsListItem(
                    title = taskToActivate.name,
                    subTitle = activator.comment?:"No comment provided",
                    emoji = taskToActivate.iconEmoji?:"🔨",
                    onPrimaryClick = {
                        navController.navigate("${AppScreens.PlayActivator.route}/${activator.activatorId}")
                    },
                    primaryIcon = Icons.Filled.PlayArrow,
                    onSecondaryClick = {
                        navController.navigate("${AppScreens.EditActivator.route}/${activator.activatorId}")
                    },
                    secondaryIcon = Icons.Filled.Edit
                )
            }
        }

        Button(
            onClick = { navController.navigate(AppScreens.CreateActivator.route) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add icon")
            Text(text = "Create new")
        }
    }
}
