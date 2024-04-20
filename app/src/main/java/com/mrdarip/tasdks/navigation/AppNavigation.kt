package com.mrdarip.tasdks.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrdarip.tasdks.screens.MainMenu
import com.mrdarip.tasdks.screens.SearchMenu

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.FirstScreen.route){
        composable(route = AppScreens.FirstScreen.route){
            MainMenu(navController)
        }
        composable(route = AppScreens.SecondScreen.route){
            SearchMenu(navController)
        }
    }
}