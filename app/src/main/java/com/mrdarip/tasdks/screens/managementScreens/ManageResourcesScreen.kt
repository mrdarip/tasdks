package com.mrdarip.tasdks.screens.managementScreens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.TwoButtonsListItem
import com.mrdarip.tasdks.navigation.AppScreens
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
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(16.dp, 0.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mainMenuState.allResources) { resource ->
                TwoButtonsListItem(
                    title = resource.name,
                    emoji = resource.resourceType.emoji,
                    primaryIcon = Icons.Default.Edit,
                    onPrimaryClick = {
                        navController.navigate("${AppScreens.CreateResource.route}/${resource.resourceId}")
                    }
                )
            }
        }

        Button(
            onClick = { navController.navigate("${AppScreens.CreateResource.route}/-1") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "New Resource")
        }
    }
}
