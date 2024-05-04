package com.mrdarip.tasdks.screens.bottomBarScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TasksCardRow
import com.mrdarip.tasdks.screens.viewModels.MainMenuState
import com.mrdarip.tasdks.screens.viewModels.MainMenuViewModel

@Composable
fun MainMenu(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    BodyContent(mainMenuViewModel = mainMenuViewModel, mainMenuState = mainMenuState)
}

@Composable
fun BodyContent(mainMenuViewModel: MainMenuViewModel, mainMenuState: MainMenuState) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        TasksCardRow(mainMenuState.tasks, "All tasks", mainMenuViewModel)
        TasksCardRow(mainMenuState.tasksOrderedByLastDone, "Last done", mainMenuViewModel)
        TasksCardRow(
            mainMenuState.tasksOrderedByUsuallyAtThisTime,
            "Usually at this time",
            mainMenuViewModel
        )
    }
}




