package com.mrdarip.tasdks.screens.bottomBarScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TasdksCard
import com.mrdarip.tasdks.composables.TaskCard
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreens
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.SearchMenuState
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.SearchMenuViewModel


@Composable
fun SearchMenu(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = SearchMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    SearchMenuBodyContent(
        mainMenuViewModel = mainMenuViewModel,
        mainMenuState = mainMenuState,
        navController = navController
    )
}

@Composable
private fun SearchMenuBodyContent(
    mainMenuViewModel: SearchMenuViewModel,
    mainMenuState: SearchMenuState,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            EntityLazyGrid(
                "Tasks",
                "Add Task",
                AppScreens.CreateTask.route,
                navController,
                mainMenuState.activeTasks
            ) { task ->
                if (task is Task)
                    TaskCard(task = task)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            EntityLazyGrid(
                "Activators",
                "Add activator",
                "${AppScreens.CreateActivator.route}/-1",
                navController,
                mainMenuState.overdueActivators
            ) { activator ->
                if (activator is Activator) {
                    val activatorTask = mainMenuViewModel.getTaskById(activator.taskToActivateId)
                        .collectAsState(initial = Task()).value
                    TasdksCard(
                        emoji = activatorTask.iconEmoji,
                        title = activatorTask.name,
                        subTitle = activator.comment
                    ) {

                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            EntityLazyGrid(
                "Resources",
                "Add Resource",
                "${AppScreens.CreateTask.route}/-1",
                navController,
                mainMenuState.allResources
            ) { task ->
                if (task is Task)
                    TaskCard(task = task)
            }
        }
    }
}

@Composable
fun EntityLazyGrid(
    title: String,
    addButtonText: String,
    addButtonRoute: String,
    navController: NavController,
    itemsToShow: List<Any> = listOf(),
    content: @Composable (Any) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineLarge)
            Button(onClick = {
                navController.navigate(addButtonRoute)
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = addButtonText
                )
                Text(text = addButtonText)
            }
        }
        LazyHorizontalGrid(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            rows = GridCells.Fixed(2), contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(itemsToShow) { item ->
                content(item)
            }
        }
    }
}