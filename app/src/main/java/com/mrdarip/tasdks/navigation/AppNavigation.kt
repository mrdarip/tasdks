package com.mrdarip.tasdks.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.mrdarip.tasdks.screens.EditTaskScreen
import com.mrdarip.tasdks.screens.MainMenu
import com.mrdarip.tasdks.screens.ManageTasksScreen
import com.mrdarip.tasdks.screens.SearchMenu
import com.mrdarip.tasdks.screens.StatsMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                Text("Top app bar")
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
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) { //2 on clicks?
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Add"
                        )
                    }
                }
            }
        ){ innerPadding ->
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
    Divider()
    val labels = listOf(
        "Manage Tasks",
        "Manage Activators",
        "Manage Objects",
        "Manage Places",
        "Manage Resources"
    )
    val icons = listOf(
        Icons.Filled.Build,
        Icons.Filled.PlayArrow,
        Icons.Filled.ShoppingCart,
        Icons.Filled.Place,
        Icons.Filled.Face
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
                    navController.navigate(AppScreens.ManageTasks.route)
                    scope.launch { drawerState.close() }
                }
            )
        }

    Divider()

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
    var selectedItem by remember {
        mutableIntStateOf(
            0
        )
    }
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



        composable(
            "${AppScreens.EditTask.route}/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId")
            EditTaskScreen(navController = navController, taskId = taskId)
        }

        composable(
            route = AppScreens.ManageTasks.route,
        ) {
            ManageTasksScreen(navController = navController)
        }
    }
}