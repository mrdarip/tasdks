package com.mrdarip.tasdks.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrdarip.tasdks.screens.MainMenu
import com.mrdarip.tasdks.screens.SearchMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(text = "paracetamol")
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text("Top app bar")
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    androidx.compose.material.icons.Icons.Filled.Home,
                                    contentDescription = ""
                                )
                            }
                        }, actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    androidx.compose.material.icons.Icons.Filled.AccountCircle,
                                    contentDescription = ""
                                )
                            }
                        })
                }, bottomBar = {
                    var selectedItem by androidx.compose.runtime.remember {
                        androidx.compose.runtime.mutableIntStateOf(
                            0
                        )
                    }
                    val items = listOf("MainMenu", "search", "Stats")

                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        androidx.compose.material.icons.Icons.Filled.Favorite,
                                        contentDescription = item
                                    )
                                },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    navController.navigate(
                                        when (index) {
                                            0 -> AppScreens.FirstScreen.route
                                            1 -> AppScreens.SecondScreen.route
                                            else -> AppScreens.FirstScreen.route
                                        }
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
                }, floatingActionButton = {
                    FloatingActionButton(onClick = {}) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.PlayArrow,
                            contentDescription = "Add"
                        )
                    }
                }) { innerPadding ->
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        16.dp
                    ),
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AppScreens.FirstScreen.route
                    ) {

                        composable(route = AppScreens.FirstScreen.route) {
                            MainMenu(navController)
                        }
                        composable(route = AppScreens.SecondScreen.route) {
                            SearchMenu(navController)
                        }
                    }
                }
            }
        }
    )
}
