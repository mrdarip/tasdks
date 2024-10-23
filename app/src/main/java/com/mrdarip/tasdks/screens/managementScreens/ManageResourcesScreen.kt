package com.mrdarip.tasdks.screens.managementScreens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TwoButtonsListItem
import com.mrdarip.tasdks.navigation.AppScreen
import com.mrdarip.tasdks.screens.managementScreens.viewModels.ManageResourcesState
import com.mrdarip.tasdks.screens.managementScreens.viewModels.ManageResourcesViewModel


@Composable
fun ManageResourcesScreen(navController: NavController) {
    val mainMenuViewModel = viewModel(modelClass = ManageResourcesViewModel::class.java)
    val mainMenuState = mainMenuViewModel.state
    ManageResourcesBodyContent(
        navController = navController,
        mainMenuState = mainMenuState,
        viewModel = mainMenuViewModel,
    )
}

@Composable
private fun ManageResourcesBodyContent(
    navController: NavController,
    mainMenuState: ManageResourcesState,
    viewModel: ManageResourcesViewModel
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp, 0.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { //TODO: make it work when clicking the card, not the icon
            TwoButtonsListItem(
                title = "Create new", onPrimaryClick = {
                    navController.navigate("${AppScreen.CreateResource.route}/-1")
                },
                primaryIcon = Icons.Default.Add,
                surfaceColor = MaterialTheme.colorScheme.secondaryContainer
            )

        }
        items(mainMenuState.allResources) { resource ->
            TwoButtonsListItem(
                title = resource.name,
                emoji = resource.resourceType.emoji,
                primaryIcon = Icons.Default.Edit,
                onPrimaryClick = {
                    navController.navigate("${AppScreen.CreateResource.route}/${resource.resourceId}")
                },
                surfaceColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    }
}

