package com.mrdarip.tasdks.navigation

sealed class AppScreens(val route: String, val isEntityScreen: Boolean = false) {
    data object FirstScreen : AppScreens("FirstScreen")
    data object SecondScreen : AppScreens("SecondScreen")
    data object ThirdScreen : AppScreens("ThirdScreen")
    data object EditActivator : AppScreens("EditActivator")
    data object ManageActivators : AppScreens("ManageActivators")
    data object CreateActivator : AppScreens("CreateActivator", true)
    data object CreateResource : AppScreens("CreateResource", true)
    data object EditResource : AppScreens("EditResource")
    data object ManageResources : AppScreens("ManageResources")
    data object ManageExecutions : AppScreens("ManageExecutions")
    data object EditTask : AppScreens("EditTask", true)
    data object ManageTasks : AppScreens("ManageTasks")
    data object CreateTask : AppScreens("CreateTask")
    data object PlayActivator : AppScreens("PlayActivator")
    companion object {
        fun valueOf(lastScreen: String): AppScreens {
            return when (lastScreen) {
                "FirstScreen" -> FirstScreen
                "SecondScreen" -> SecondScreen
                "ThirdScreen" -> ThirdScreen
                "EditActivator" -> EditActivator
                "ManageActivators" -> ManageActivators
                "CreateActivator" -> CreateActivator
                "CreateResource" -> CreateResource
                "EditResource" -> EditResource
                "ManageResources" -> ManageResources
                "ManageExecutions" -> ManageExecutions
                "EditTask" -> EditTask
                "ManageTasks" -> ManageTasks
                "CreateTask" -> CreateTask
                "PlayActivator" -> PlayActivator
                else -> FirstScreen
            }
        }
    }
}