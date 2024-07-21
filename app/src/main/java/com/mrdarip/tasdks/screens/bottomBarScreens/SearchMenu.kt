package com.mrdarip.tasdks.screens.bottomBarScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuState
import com.mrdarip.tasdks.screens.bottomBarScreens.viewModels.MainMenuViewModel


@Composable
fun SearchMenu(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    SearchMenuBodyContent(mainMenuViewModel = mainMenuViewModel, mainMenuState = mainMenuState)
}

@Composable
fun SearchMenuBodyContent(mainMenuViewModel: MainMenuViewModel, mainMenuState: MainMenuState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            EntityLazyGrid(mainMenuState.activeTasks) { task ->
                if (task is Task)
                    TaskCard(task = task)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            EntityLazyGrid(mainMenuState.overdueActivators) { a ->
                if (a is Activator) {
                    val activatorTask = mainMenuViewModel.getTaskById(a.taskToActivateId)
                        .collectAsState(initial = Task()).value
                    TasdksCard(
                        emoji = activatorTask.iconEmoji ?: "",
                        title = activatorTask.name,
                        subTitle = a.comment
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
fun EntityLazyGrid(itemsToShow: List<Any> = listOf(), content: @Composable (Any) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Tasks", style = MaterialTheme.typography.headlineLarge)
            Button(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Add Task"
                )
                Text(text = "Add Task")
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