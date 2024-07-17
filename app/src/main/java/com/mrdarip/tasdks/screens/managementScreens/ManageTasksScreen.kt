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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TwoButtonsListItem
import com.mrdarip.tasdks.navigation.AppScreens
import com.mrdarip.tasdks.screens.viewModels.MainMenuState
import com.mrdarip.tasdks.screens.viewModels.MainMenuViewModel

@Composable
fun ManageTasksScreen(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    ManageTasksBodyContent(navController = navController, mainMenuViewModel = mainMenuViewModel, mainMenuState = mainMenuState)
}

@Composable
fun ManageTasksBodyContent(navController: NavController, mainMenuViewModel: MainMenuViewModel, mainMenuState: MainMenuState) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(0.dp, 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mainMenuState.activeTasks) { task ->
                val placeName by mainMenuViewModel.getPlaceName(task.placeId)
                    .collectAsState(initial = "")

                TwoButtonsListItem(
                    title = task.name,
                    subTitle = "",
                    emoji = task.iconEmoji ?: "🔨",
                    primaryIcon = Icons.Filled.PlayArrow,
                    secondaryIcon = Icons.Filled.Add,
                    onPrimaryClick = {
                        //TODO: implement creating one time activator
                    },
                    onSecondaryClick = {
                        //TODO: implement creating activator
                        navController.navigate(
                            "${AppScreens.CreateActivator.route}/${task.taskId}"
                        )
                    }
                )
            }
        }

        Button(
            onClick = { navController.navigate(AppScreens.CreateTask.route) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "New Task")
        }
    }
}

