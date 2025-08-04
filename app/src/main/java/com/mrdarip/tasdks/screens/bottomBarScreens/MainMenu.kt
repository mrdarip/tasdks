package com.mrdarip.tasdks.screens.bottomBarScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.ActivatorCardRow
import com.mrdarip.tasdks.composables.TasksCardRow
import com.mrdarip.tasdks.navigation.AppScreen
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuState
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuViewModel

@Composable
fun MainMenu(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    BodyContent(
        mainMenuViewModel = mainMenuViewModel,
        mainMenuState = mainMenuState,
        navController = navController
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun BodyContent(
    mainMenuViewModel: MainMenuViewModel,
    mainMenuState: MainMenuState,
    navController: NavController
) {
    if (mainMenuState.activeTasks.isEmpty() && mainMenuState.overdueActivatorsWithStats.isEmpty() && mainMenuState.pendingActivatorsWithStats.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "There's no task yet. Please add a task first.\nฅ^•ﻌ•^ฅ",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { navController.navigate(AppScreen.CreateTask.route) }
            ) {
                Text("Add Task")
            }
        }
    } else {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            ActivatorCardRow(
                mainMenuState.overdueActivatorsWithStats,
                "Overdue Tasks",
                navController
            )
            ActivatorCardRow(
                mainMenuState.pendingActivatorsWithStats,
                "Pending Tasks",
                navController
            )

            TasksCardRow(mainMenuState.activeTasks, "All tasks", mainMenuViewModel, navController)
            TasksCardRow(
                mainMenuState.tasksOrderedByLastDone,
                "Last done",
                mainMenuViewModel,
                navController
            )
            TasksCardRow(
                mainMenuState.tasksOrderedByUsuallyAtThisTime,
                "Usually at this time",
                mainMenuViewModel,
                navController
            )
        }
    }
}



