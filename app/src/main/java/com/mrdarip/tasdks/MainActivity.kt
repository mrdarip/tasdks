package com.mrdarip.tasdks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrdarip.tasdks.navigation.AppNavigation
import com.mrdarip.tasdks.navigation.AppScreens
import com.mrdarip.tasdks.screens.MainMenu
import com.mrdarip.tasdks.screens.MainMenuViewModel
import com.mrdarip.tasdks.screens.SearchMenu
import com.mrdarip.tasdks.ui.theme.TasdksTheme
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasdksTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val mainMenuViewModel = viewModel(modelClass = MainMenuViewModel::class.java)
                    val mainMenuState = mainMenuViewModel.state

                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    val navController = rememberNavController()



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
                                                    Icons.Filled.Home,
                                                    contentDescription = ""
                                                )
                                            }
                                        }, actions = {
                                            IconButton(onClick = { /*TODO*/ }) {
                                                Icon(
                                                    Icons.Filled.AccountCircle,
                                                    contentDescription = ""
                                                )
                                            }
                                        })
                                }, bottomBar = {
                                    var selectedItem by remember { mutableIntStateOf(0) }
                                    val items = listOf("MainMenu", "search", "Stats")

                                    NavigationBar {
                                        items.forEachIndexed { index, item ->
                                            NavigationBarItem(
                                                icon = {
                                                    Icon(
                                                        Icons.Filled.Favorite,
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
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }, floatingActionButton = {
                                    FloatingActionButton(onClick = {}) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Add")
                                    }
                                }) { innerPadding ->
                                Column(
                                    modifier = Modifier.padding(innerPadding),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    AppNavigation(navController = navController)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}