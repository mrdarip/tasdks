package com.mrdarip.tasdks.navigation

sealed class AppScreens(val route: String) {
    object firstScreen : AppScreens("firstScreen")
    object secondScreen : AppScreens("secondScreen")
}