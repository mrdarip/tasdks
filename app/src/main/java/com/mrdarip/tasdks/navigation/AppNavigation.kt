package com.mrdarip.tasdks.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mrdarip.tasdks.composables.EntitiesBackHandler
import com.mrdarip.tasdks.composables.TwoButtonsListItem
import com.mrdarip.tasdks.data.entity.Execution
import com.mrdarip.tasdks.data.entity.IDRoute
import com.mrdarip.tasdks.screens.AboutScreen
import com.mrdarip.tasdks.screens.NotFoundScreen
import com.mrdarip.tasdks.screens.bottomBarScreens.MainMenu
import com.mrdarip.tasdks.screens.bottomBarScreens.SearchMenu
import com.mrdarip.tasdks.screens.bottomBarScreens.StatsMenu
import com.mrdarip.tasdks.screens.managementScreens.CreateActivatorScreen
import com.mrdarip.tasdks.screens.managementScreens.CreateTaskScreen
import com.mrdarip.tasdks.screens.managementScreens.EditActivatorScreen
import com.mrdarip.tasdks.screens.managementScreens.EditTaskScreen
import com.mrdarip.tasdks.screens.managementScreens.ManageActivatorsScreen
import com.mrdarip.tasdks.screens.managementScreens.ManageExecutionsScreen
import com.mrdarip.tasdks.screens.managementScreens.ManageTasksScreen
import com.mrdarip.tasdks.screens.playScreens.ManageRunningExecutions
import com.mrdarip.tasdks.screens.playScreens.PlayExecutionScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentRoute by rememberSaveable { mutableStateOf("FirstScreen") }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        currentRoute = destination.route ?: "FirstScreen"
    }

    EntitiesBackHandler(
        navController = navController,
        currentRoute = currentRoute
    )

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            DrawerContent(navController, drawerState, scope)
        }
    }, content = {
        Scaffold(topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(currentRoute)
            }, navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = ""
                    )
                }
            }, actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = ""
                    )
                }
            })
        },
            bottomBar = {
                BottomBar(navController = navController)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(
                    16.dp
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MainNavHost(navController)
                    RunningActivators()
                }
            }
        }
    })
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Column {
        Text("Manage", modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
        HorizontalDivider()
        val labels = listOf(
            "Manage Tasks",
            "Manage Activators",
            "Manage Running Executions"
        )

        val icons = listOf(
            Icons.Filled.Build,
            Icons.Filled.PlayArrow,
            Icons.Filled.Call
        )

        val screens = listOf(
            AppScreen.ManageTasks,
            AppScreen.ManageActivators,
            AppScreen.ManageRunningExecutions

        )

        labels.forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = { Text(item) },
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = item
                    )
                },
                selected = false,
                onClick = {
                    navController.navigate(screens[index].route) {
                        popUpTo(screens[index].route) {
                            inclusive = true
                        }
                    }

                    scope.launch { drawerState.close() }
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text(text = "Settings") },
            icon = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            },
            selected = false,
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        NavigationDrawerItem(
            label = { Text(text = "About") },
            icon = {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "About"
                )
            },
            selected = false,
            onClick = { 
                navController.navigate(AppScreen.About.route)
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun BottomBar(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf("Menu", "Search", "Stats")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.BarChart)
    val bottomBarScreenRoutes = listOf(
        AppScreen.FirstScreen.route,
        AppScreen.SecondScreen.route,
        AppScreen.ThirdScreen.route,
    )

    navController.addOnDestinationChangedListener { _, destination, _ ->
        selectedItem = bottomBarScreenRoutes.indexOf(destination.route)
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = item
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index

                    navController.navigate(bottomBarScreenRoutes[index])
                }
            )
        }
    }
}

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppScreen.FirstScreen.route) {
        composable(route = AppScreen.FirstScreen.route) {
            MainMenu(navController)
        }
        composable(route = AppScreen.SecondScreen.route) {
            SearchMenu(navController)
        }
        composable(route = AppScreen.ThirdScreen.route) {
            StatsMenu(navController)
        }

        composable(route = AppScreen.ManageExecutions.route) {
            ManageExecutionsScreen(navController = navController)
        }

        composable(
            "${AppScreen.EditTask.route}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
            EditTaskScreen(
                navController = navController,
                taskId = taskId ?: 0
            )
        }

        composable(
            route = AppScreen.ManageTasks.route,
        ) {
            ManageTasksScreen(navController = navController)
        }

        composable(
            route = AppScreen.CreateTask.route,
        ) {
            CreateTaskScreen(navController = navController)
        }

        composable(
            AppScreen.ManageActivators.route,
        ) {
            ManageActivatorsScreen(navController = navController)
        }

        composable(
            AppScreen.ManageRunningExecutions.route,
        ) {
            ManageRunningExecutions(navController = navController)
        }

        composable(
            "${AppScreen.CreateActivator.route}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) {
            CreateActivatorScreen(
                navController,
                preSelectedTaskId = it.arguments?.getLong("taskId") ?: -1
            )
        }

        composable(
            "${AppScreen.EditActivator.route}/{activatorId}",
            arguments = listOf(navArgument("activatorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activatorId = backStackEntry.arguments?.getLong("activatorId")
            EditActivatorScreen(navController = navController, activatorId = activatorId ?: 0)
        }

        composable(
            AppScreen.NotFound.route
        ) {
            NotFoundScreen()
        }
        
        composable(
            AppScreen.About.route
        ) {
            AboutScreen()
        }

        composable(
            "${AppScreen.PlayExecution.route}/execution/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")

            PlayExecutionScreen(
                navController = navController,
                navigationArgs = Execution(
                    executionId = id ?: 0,
                    taskId = 0,
                    tasksRoute = IDRoute(emptyList()),
                    executionRoute = IDRoute(emptyList()),
                    childNumber = 0
                )
            )
        }

        composable(
            "${AppScreen.PlayExecution.route}/task/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")

            PlayExecutionScreen(
                navController = navController,
                navigationArgs = Execution(
                    executionId = 0,
                    taskId = id ?: 0,
                    tasksRoute = IDRoute(emptyList()),
                    executionRoute = IDRoute(emptyList()),
                    childNumber = 0
                )
            )
        }

        composable(
            "${AppScreen.PlayExecution.route}/activator/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id")

            PlayExecutionScreen(
                navController = navController,
                navigationArgs = Execution(
                    executionId = 0,
                    taskId = 0,
                    activatorId = id ?: 0,
                    tasksRoute = IDRoute(emptyList()),
                    executionRoute = IDRoute(emptyList()),
                    childNumber = 0
                )
            )
        }

    }
}

@Composable
fun RunningActivators() {
    val viewModel = viewModel(modelClass = WidgetBottomBarViewModel::class.java)
    val viewModelState = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.Bottom
        ), // Space and align to bottom
    ) {
        viewModelState.executions.forEach { execution ->
            TwoButtonsListItem(
                title = execution.executionId.toString()
            )
        }
    }
}