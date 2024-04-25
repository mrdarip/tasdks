package com.mrdarip.tasdks.navigation

sealed class AppScreens(val route: String) {
    data object FirstScreen : AppScreens("FirstScreen")
    data object SecondScreen : AppScreens("SecondScreen")
    data object ThirdScreen : AppScreens("ThirdScreen")
    data object EditActivator : AppScreens("EditActivator")
    data object ManageActivators : AppScreens("ManageActivators")
    data object EditExecution : AppScreens("EditExecution")
    data object ManageExecutions : AppScreens("ManageExecutions")
    data object EditObject : AppScreens("EditObject")
    data object ManageObjects : AppScreens("ManageObjects")
    data object EditPlace : AppScreens("EditPlace")
    data object ManagePlaces : AppScreens("ManagePlaces")
    data object EditResource : AppScreens("EditPlace")
    data object ManageResources : AppScreens("ManagePlaces")
    data object EditTask : AppScreens("EditTask")
    data object ManageTasks : AppScreens("ManageTasks")

}