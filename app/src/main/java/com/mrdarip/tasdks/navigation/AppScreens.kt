package com.mrdarip.tasdks.navigation

sealed class AppScreens(val route: String) {
    data object FirstScreen : AppScreens("FirstScreen")
    data object SecondScreen : AppScreens("SecondScreen")
    data object ThirdScreen : AppScreens("ThirdScreen")
    data object EditActivator : AppScreens("EditActivator")
    data object ManageActivators : AppScreens("ManageActivators")
    data object CreateActivator : AppScreens("CreateActivator")
    data object EditResource : AppScreens("EditResource")
    data object ManageResources : AppScreens("ManageResources")
    data object EditTask : AppScreens("EditTask")
    data object ManageTasks : AppScreens("ManageTasks")
    data object CreateTask : AppScreens("CreateTask")
    data object PlayActivator : AppScreens("PlayActivator")
}