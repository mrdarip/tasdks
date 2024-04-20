package com.mrdarip.tasdks.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mrdarip.tasdks.data.entity.Task
import com.mrdarip.tasdks.navigation.AppNavigation
import com.mrdarip.tasdks.navigation.AppScreens
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
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


@Composable
fun TaskCard(task: Task, mainMenuViewModel: MainMenuViewModel, onClick: () -> Unit = {}) {
    val placeName by mainMenuViewModel.getPlaceName(task.placeId).collectAsState(initial = "")

    Box(modifier = Modifier) {

        Column(
            verticalArrangement = Arrangement.Top, modifier = Modifier
                .width(150.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Text(
                text = (task.iconEmoji ?: "🗒️") + " " + task.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                text = task.comment ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = placeName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

        }
        Text(
            text = task.iconEmoji ?: "🗒️",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.absoluteOffset(12.dp, (-12).dp)
        )
    }

}

@Composable
fun TasksCardRow(tasks: List<Task>, title: String, mainMenuViewModel: MainMenuViewModel) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(16.dp, 32.dp, 16.dp, 8.dp)
            .fillMaxWidth()

    )

    LazyRow(
        modifier = Modifier.padding(0.dp, 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tasks) { task ->
            TaskCard(
                task = task,
                mainMenuViewModel = mainMenuViewModel,
                onClick = {
                    mainMenuViewModel.deleteTask(task)
                }
            )
        }
    }
}