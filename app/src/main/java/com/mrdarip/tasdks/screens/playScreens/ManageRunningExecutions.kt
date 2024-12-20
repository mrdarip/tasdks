package com.mrdarip.tasdks.screens.playScreens

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ManageRunningExecutions(navController: NavController) {
    val viewModel = viewModel(modelClass = ManageRunningExecutionsViewModel::class.java)

    ManageRunningExecutionsBodyContent(
        viewModel = viewModel, navController = navController
    )
}

@Composable
private fun ManageRunningExecutionsBodyContent(
    viewModel: ManageRunningExecutionsViewModel, navController: NavController
) {
    //scrollable
    //show all running executions
    LazyColumn {
        items(viewModel.state.executions) { execution ->
            Log.i("ManageRunningExecutions", "Execution: $execution")
        }
    }
}