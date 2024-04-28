package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.screens.managementScreens.viewModels.EditTaskViewModel

@Composable
fun CreateTaskScreen(navController: NavController) {
    val editTaskViewModel = viewModel(modelClass = EditTaskViewModel::class.java)
    CreateTaskBodyContent(
        navController = navController,
        editTaskViewModel = editTaskViewModel
    )
}

@Composable
fun CreateTaskBodyContent(navController: NavController, editTaskViewModel: EditTaskViewModel) {

}