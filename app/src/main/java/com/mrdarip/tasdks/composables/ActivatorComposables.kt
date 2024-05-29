package com.mrdarip.tasdks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.data.entity.Activator


@Composable
fun EditActivatorListItem(
    title: String,
    subTitle: String,
    emoji: String,
    primaryIcon: ImageVector,
    onPrimaryClick: () -> Unit = {},
    secondaryIcon: ImageVector,
    onSecondaryClick: () -> Unit = {},
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
            Row {
                IconButton(onClick = { onSecondaryClick() }) {
                    Icon(
                        imageVector = secondaryIcon,
                        contentDescription = "Edit Activator",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,

                        )
                }

                IconButton(onClick = { onPrimaryClick() }) {
                    Icon(
                        imageVector = primaryIcon,
                        contentDescription = "Play Activator",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
        }
    }
}


@Composable
fun ActivatorCardRow(
    tasks: List<Activator>,
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(16.dp, 32.dp, 16.dp, 8.dp)
            .fillMaxWidth()

    )

    LazyRow(
        modifier = Modifier.padding(0.dp, 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(tasks) { activator ->
            ActivatorCard(
                activator = activator,
                onClick = {}
            )
        }
    }
}

@Composable
fun ActivatorCard(activator: Activator, onClick: () -> Unit = {}) {

    Column(
        verticalArrangement = Arrangement.Top, modifier = Modifier
            .width(150.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = activator.comment ?: "Hola",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        Text(
            text = activator.activatorId.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

}