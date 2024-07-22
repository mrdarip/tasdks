package com.mrdarip.tasdks.screens.managementScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrdarip.tasdks.composables.ResourceFields
import com.mrdarip.tasdks.data.entity.Resource
import com.mrdarip.tasdks.data.entity.ResourceType
import com.mrdarip.tasdks.screens.managementScreens.viewModels.CreateTaskViewModel

@Composable
fun CreateResourceScreen(navController: NavController) {
    val createTaskViewModel = viewModel(modelClass = CreateTaskViewModel::class.java)
    CreateResourceBodyContent(
        navController = navController,
        createTaskViewModel = createTaskViewModel
    )
}

@Composable
fun CreateResourceBodyContent(
    navController: NavController,
    createTaskViewModel: CreateTaskViewModel
) {
    var resource by remember { mutableStateOf(Resource(resourceType = ResourceType.VIDEO)) }
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Create Resource", style = MaterialTheme.typography.headlineLarge)
            ResourceFields(
                resource = resource,
                onResourceChanged = { resource = it }
            )
        }
        Button(onClick = {
            createTaskViewModel.insertResource(resource)
        }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Resource")
            Text("Add")
        }
    }

}

