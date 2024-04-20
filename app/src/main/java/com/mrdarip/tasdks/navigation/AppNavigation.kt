package com.mrdarip.tasdks.navigation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrdarip.tasdks.screens.MainMenu
import com.mrdarip.tasdks.screens.SearchMenu
import com.mrdarip.tasdks.screens.StatsMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            DrawerContent()
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
        }, bottomBar = {
            BottomBar(navController = navController)
        }, floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                IconButton(onClick = {scope.launch{drawerState.open()}}) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Add"
                    )
                }
            }
        }) { innerPadding ->
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(
                    16.dp
                ),
            ) {
                NavHost(
                    navController = navController, startDestination = AppScreens.FirstScreen.route
                ) {

                    composable(route = AppScreens.FirstScreen.route) {
                        MainMenu(navController)
                    }
                    composable(route = AppScreens.SecondScreen.route) {
                        SearchMenu(navController)
                    }
                    composable(route = AppScreens.ThirdScreen.route) {
                        StatsMenu(navController)
                    }
                }
            }
        }
    })
}

@Composable
fun DrawerContent(){
    Text("Drawer title", modifier = Modifier.padding(16.dp))
    Divider()
    NavigationDrawerItem(
        label = { Text(text = "Manage Tasks") },
        icon = {
            Icon(
                Icons.Filled.Build,
                contentDescription = "Tasks"
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )
    NavigationDrawerItem(
        label = { Text(text = "Manage Activators") },
        icon = {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Places"
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )
    NavigationDrawerItem(
        label = { Text(text = "Manage Objects") },
        icon = {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Objects"
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )
    NavigationDrawerItem(
        label = { Text(text = "Manage Places") },
        icon = {
            Icon(
                Icons.Filled.Place,
                contentDescription = "Places"
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )
    NavigationDrawerItem(
        label = { Text(text = "Manage Resources") },
        icon = {
            Icon(
                Icons.Filled.Face,
                contentDescription = "Resources"
            )
        },
        selected = false,
        onClick = { /*TODO*/ }
    )

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
fun BottomBar(navController: NavController){
    var selectedItem by remember {
        androidx.compose.runtime.mutableIntStateOf(
            0
        )
    }
    val items = listOf("Menu", "Search", "Stats")
    val icons = listOf(Icons.Filled.Home,Icons.Filled.Search,Icons.Filled.AccountCircle)

    navController.addOnDestinationChangedListener { _, destination, _ ->
        when (destination.route) {
            AppScreens.FirstScreen.route -> selectedItem = 0
            AppScreens.SecondScreen.route -> selectedItem = 1
        }
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(icon = {
                Icon(
                    icons[index],
                    contentDescription = item
                )
            }, label = { Text(item) }, selected = selectedItem == index, onClick = {
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
            })
        }
    }
}