package com.mrdarip.tasdks.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp


@Composable
fun EditActivatorListItem(
    title:String, subTitle: String, emoji:String, onEditClick: () -> Unit = {}, onPlayClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                modifier = Modifier.weight(1f), // This row will occupy the left space
                ) {
                Text(
                    emoji,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Column {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = subTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row() {
                IconButton(onClick = { onEditClick() }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Activator",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,

                    )
                }

                IconButton(onClick = { onPlayClick() }) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play Activator",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
        }
    }
}