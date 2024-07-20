package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.screens.managementScreens.viewModels.ManageActivatorsViewModel


@Composable
fun ManageExecutionsScreen(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = ManageActivatorsViewModel::class.java)
    ManageExecutionsBodyContent(
        navController = navController,
        viewModel = mainMenuViewModel,
    )
}

@Composable
fun ManageExecutionsBodyContent(
    navController: NavController,
    viewModel: ManageActivatorsViewModel
) {
    Text("Manage Executions Screen")
}
