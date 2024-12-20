package com.mrdarip.tasdks.navigation

enum class AppScreen(val route: String, val isEntityScreen: Boolean = false) {
    FirstScreen("FirstScreen", false),
    SecondScreen("SecondScreen", false),
    ThirdScreen("ThirdScreen", false),
    EditActivator("EditActivator", true),
    ManageActivators("ManageActivators", false),
    CreateActivator("CreateActivator", true),
    ManageExecutions("ManageExecutions", false),
    EditTask("EditTask", true),
    ManageTasks("ManageTasks", false),
    CreateTask("CreateTask", true),
    PlayActivator("PlayActivator", true),
    NotFound("NotFound", false),
    ManageRunningExecutions("ManageRunningExecutions");

    companion object {
        fun fromRoute(route: String): AppScreen {
            return valueOf(route.split(".").last().split("/").first())
        }
    }
}