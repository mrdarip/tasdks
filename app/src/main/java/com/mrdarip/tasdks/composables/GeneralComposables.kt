package com.mrdarip.tasdks.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TwoButtonsListItem(
    title: String,
    subTitle: String,
    emoji: String? = null,
    primaryIcon: ImageVector? = null,
    onPrimaryClick: () -> Unit = {},
    secondaryIcon: ImageVector? = null,
    onSecondaryClick: () -> Unit = {},
    onLiItemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLiItemClick),
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

                if (emoji != null) {
                    Text(
                        emoji,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

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
                if (secondaryIcon != null) {
                    IconButton(onClick = { onSecondaryClick() }) {
                        Icon(
                            imageVector = secondaryIcon,
                            contentDescription = "Edit Activator",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,

                            )
                    }
                }
                if (primaryIcon != null) {
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
}

@Composable
fun ThreeButtonsListItem(
    title: String,
    subTitle: String,
    emoji: String? = null,
    primaryIcon: ImageVector? = null,
    onPrimaryClick: () -> Unit = {},
    secondaryIcon: ImageVector? = null,
    onSecondaryClick: () -> Unit = {},
    tertiaryIcon: ImageVector? = null,
    onTertiaryClick: () -> Unit = {},
    onLiItemClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLiItemClick),
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

                if (emoji != null) {
                    Text(
                        emoji,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

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
                if (tertiaryIcon != null) {
                    IconButton(onClick = { onTertiaryClick() }) {
                        Icon(
                            imageVector = tertiaryIcon,
                            contentDescription = "Edit Activator",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,

                            )
                    }
                }
                if (secondaryIcon != null) {
                    IconButton(onClick = { onSecondaryClick() }) {
                        Icon(
                            imageVector = secondaryIcon,
                            contentDescription = "Edit Activator",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,

                            )
                    }
                }
                if (primaryIcon != null) {
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
}