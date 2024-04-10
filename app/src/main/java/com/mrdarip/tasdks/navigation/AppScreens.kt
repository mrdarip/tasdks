package com.mrdarip.tasdks.navigation

sealed class AppScreens(val route: String) {
    object FirstScreen : AppScreens("FirstScreen")
    object SecondScreen : AppScreens("SecondScreen")
}