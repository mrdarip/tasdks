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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.data.entity.Activator
import com.mrdarip.tasdks.data.entity.RepetitionRange
import com.mrdarip.tasdks.data.entity.RepetitionType

@Preview
@Composable
fun EditActivatorListItem(
    activator: Activator = Activator(
        0, "ah carajoooo", RepetitionRange(0, 0, 0, RepetitionType.YEARS), null, 1, true, 2
    ), onEditClick: () -> Unit = {}, onPlayClick: () -> Unit = {}
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

                ) {
                Text(
                    "😎",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Column {
                    Text(
                        text = activator.taskToActivateId.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = activator.comment ?: "NO DESCRIPTION",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row {
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