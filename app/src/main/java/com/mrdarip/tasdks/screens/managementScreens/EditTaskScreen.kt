package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.screens.bottomBarScreens.MainMenuState
import com.mrdarip.tasdks.screens.bottomBarScreens.MainMenuViewModel

@Composable
fun EditTaskScreen(navController: NavController, taskId: Long?) {
    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    EditTaskBodyContent(mainMenuViewModel = mainMenuViewModel, mainMenuState = mainMenuState, taskId = taskId)
}

@Composable
fun EditTaskBodyContent(mainMenuViewModel: MainMenuViewModel, mainMenuState: MainMenuState, taskId: Long?) {
    val task by mainMenuViewModel.getTaskById(taskId ?: 0).collectAsState(initial = null)

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(text = task?.name ?: "Task not found")

    }
}