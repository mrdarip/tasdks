package com.mrdarip.tasdks.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mrdarip.tasdks.screens.bottomBarScreens.MainMenu
import com.mrdarip.tasdks.screens.bottomBarScreens.SearchMenu
import com.mrdarip.tasdks.screens.bottomBarScreens.StatsMenu
import com.mrdarip.tasdks.screens.managementScreens.CreateActivatorScreen
import com.mrdarip.tasdks.screens.managementScreens.CreateResourceScreen
import com.mrdarip.tasdks.screens.managementScreens.CreateTaskScreen
import com.mrdarip.tasdks.screens.managementScreens.EditActivatorScreen
import com.mrdarip.tasdks.screens.managementScreens.EditTaskScreen
import com.mrdarip.tasdks.screens.managementScreens.ManageActivatorsScreen
import com.mrdarip.tasdks.screens.managementScreens.ManageExecutionsScreen
import com.mrdarip.tasdks.screens.managementScreens.ManageResourcesScreen
import com.mrdarip.tasdks.screens.managementScreens.ManageTasksScreen
import com.mrdarip.tasdks.screens.playScreens.PlayActivatorScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var currentRoute by rememberSaveable { mutableStateOf("Default Screen") }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        currentRoute = destination.route ?: "Default Screen"
    }

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
                MainNavHost(navController = navController)
            }
        }
    })
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Text("Manage", modifier = Modifier.padding(16.dp))
    HorizontalDivider()
    val labels = listOf(
        "Manage Tasks",
        "Manage Activators",
        "Manage Resources"
    )

    val icons = listOf(
        Icons.Filled.Build,
        Icons.Filled.PlayArrow,
        Icons.Filled.Star
    )

    val screens = listOf(
        AppScreens.ManageTasks,
        AppScreens.ManageActivators,
        AppScreens.ManageResources
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
                navController.navigate(screens[index].route)
                scope.launch { drawerState.close() }
            }
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
        onClick = { /*TODO*/ }
    )
}

@Composable
fun BottomBar(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf("Menu", "Search", "Stats")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.AccountCircle)
    val bottomBarScreenRoutes = listOf(
        AppScreens.FirstScreen.route,
        AppScreens.SecondScreen.route,
        AppScreens.ThirdScreen.route,
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
                    // Check if the current screen is not one of the bottom bar screens
                    if (!bottomBarScreenRoutes.contains(
                            navController.currentDestination?.route ?: ""
                        )
                    ) {
                        // If it is, pop the back stack to close the EditTask screen
                        navController.popBackStack()
                    }

                    navController.navigate(
                        bottomBarScreenRoutes.getOrElse(index) { AppScreens.FirstScreen.route }
                    ) {
                        // Avoid recreating the screen if it's already on the back stack
                        launchSingleTop = true
                        // Pop up to the start destination before navigating
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Restore state when reusing a screen
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppScreens.FirstScreen.route) {
        composable(route = AppScreens.FirstScreen.route) {
            MainMenu(navController)
        }
        composable(route = AppScreens.SecondScreen.route) {
            SearchMenu(navController)
        }
        composable(route = AppScreens.ThirdScreen.route) {
            StatsMenu(navController)
        }

        composable(route = AppScreens.ManageResources.route) {
            ManageResourcesScreen(navController = navController)
        }

        composable(route = AppScreens.CreateResource.route) {
            CreateResourceScreen(navController = navController)
        }

        composable(route = AppScreens.ManageExecutions.route) {
            ManageExecutionsScreen(navController = navController)
        }
        composable(
            "${AppScreens.EditTask.route}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
            EditTaskScreen(
                navController = navController,
                taskId = taskId?:0
            )
        }

        composable(
            route = AppScreens.ManageTasks.route,
        ) {
            ManageTasksScreen(navController = navController)
        }

        composable(
            route = AppScreens.CreateTask.route,
        ) {
            CreateTaskScreen(navController = navController)
        }

        composable(
            "${AppScreens.PlayActivator.route}/{activatorId}",
            arguments = listOf(navArgument("activatorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activatorId = backStackEntry.arguments?.getLong("activatorId")
            PlayActivatorScreen(activatorId ?: 0, navController)
        }

        composable(
            AppScreens.ManageActivators.route,
        ) {
            ManageActivatorsScreen(navController = navController)
        }

        composable("${AppScreens.CreateActivator.route}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) {
            CreateActivatorScreen(
                navController,
                preSelectedTaskId = it.arguments?.getLong("taskId") ?: -1
            )
        }

        composable(
            "${AppScreens.EditActivator.route}/{activatorId}",
            arguments = listOf(navArgument("activatorId") { type = NavType.LongType })
        ) { backStackEntry ->
            val activatorId = backStackEntry.arguments?.getLong("activatorId")
            EditActivatorScreen(navController = navController, activatorId = activatorId ?: 0)
        }
    }
}