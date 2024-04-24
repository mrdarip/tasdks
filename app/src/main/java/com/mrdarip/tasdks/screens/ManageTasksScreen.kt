package com.mrdarip.tasdks.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppScreens

@Composable
fun ManageTasksScreen(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    ManageTasksBodyContent(navController = navController, mainMenuViewModel = mainMenuViewModel, mainMenuState = mainMenuState)
}

@Composable
fun ManageTasksBodyContent(navController: NavController,mainMenuViewModel: MainMenuViewModel, mainMenuState: MainMenuState) {
    LazyColumn(
        modifier = Modifier.padding(0.dp, 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(mainMenuState.tasks) { task ->
            val placeName by mainMenuViewModel.getPlaceName(task.placeId)
                .collectAsState(initial = "")
            TaskLiItem(
                task = task,
                placeName = placeName,
                onClick = {
                    navController.navigate(AppScreens.EditTask.route +"/"+task.taskId)
                }
            )
        }
    }
}

@Composable
fun TaskLiItem(task: Task, placeName: String, onClick: () -> Unit = {}) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {// TODO: Add task min/opt/max
        Row(horizontalArrangement = Arrangement.spacedBy(space = 8.dp), modifier = Modifier.padding(16.dp)){
            Text(
                task.iconEmoji ?: "🤕",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Column {
                Text(
                    text = task.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = task.comment ?: "NO DESCRIPTION",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = placeName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskLiItemPreview() {
    TaskLiItem(
        task = Task(
            name = "Task name",
            comment = "Task comment",
            iconEmoji = "🐱",
            placeId = null
        ),
        placeName = "Place name"
    )
}