package com.mrdarip.tasdks.navigation

sealed class AppScreen(val route: String, val isEntityScreen: Boolean = false) {
    data object FirstScreen : AppScreen("FirstScreen")
    data object SecondScreen : AppScreen("SecondScreen")
    data object ThirdScreen : AppScreen("ThirdScreen")
    data object EditActivator : AppScreen("EditActivator")
    data object ManageActivators : AppScreen("ManageActivators")
    data object CreateActivator : AppScreen("CreateActivator", true)
    data object CreateResource : AppScreen("CreateResource", true)
    data object EditResource : AppScreen("EditResource")
    data object ManageResources : AppScreen("ManageResources")
    data object ManageExecutions : AppScreen("ManageExecutions")
    data object EditTask : AppScreen("EditTask", true)
    data object ManageTasks : AppScreen("ManageTasks")
    data object CreateTask : AppScreen("CreateTask")
    data object PlayActivator : AppScreen("PlayActivator")
    companion object {
        fun valueOf(lastScreen: String): AppScreen {
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