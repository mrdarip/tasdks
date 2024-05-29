package com.mrdarip.tasdks.screens.bottomBarScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.ActivatorCardRow
import com.mrdarip.tasdks.composables.TasksCardRow
import com.mrdarip.tasdks.screens.viewModels.MainMenuState
import com.mrdarip.tasdks.screens.viewModels.MainMenuViewModel

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
fun BodyContent(
    mainMenuViewModel: MainMenuViewModel,
    mainMenuState: MainMenuState,
    navController: NavController
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        ActivatorCardRow(mainMenuState.overdueActivators, "Overdue Tasks")
        ActivatorCardRow(mainMenuState.pendingActivators, "Pending Tasks")

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




