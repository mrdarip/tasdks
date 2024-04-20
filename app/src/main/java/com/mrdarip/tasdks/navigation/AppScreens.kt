package com.mrdarip.tasdks.navigation

sealed class AppScreens(val route: String) {
    data object FirstScreen : AppScreens("FirstScreen")
    data object SecondScreen : AppScreens("SecondScreen")
    data object ThirdScreen : AppScreens("ThirdScreen")
}