package com.mrdarip.tasdks.composables.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.data.entity.Resource
import com.mrdarip.tasdks.data.entity.ResourceType
import java.util.Locale

@Composable
fun ResourceFields(
    resource: Resource = Resource(resourceType = ResourceType.VIDEO),
    onResourceChanged: (Resource) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = resource.name,
            onValueChange = { onResourceChanged(resource.copy(name = it)) },
            label = { Text("Name") },
            placeholder = { Text("Resource name") },
            modifier = Modifier.fillMaxWidth()
        )



        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(ResourceType.entries) { option ->
                val selected = resource.resourceType == option
                FilterChip(
                    onClick = {
                        onResourceChanged(
                            resource.copy(
                                resourceType = option
                            )
                        )
                    },
                    label = {
                        Text(capitalized(option.name))
                    },
                    selected = selected,
                    leadingIcon = {
                        if (selected) {

                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                        } else {
                            Text(
                                option.emoji,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )

                        }
                    },
                )
            }
        }
    }
}

//todo: move to utils?
fun capitalized(text: String): String {
    return text.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }
}