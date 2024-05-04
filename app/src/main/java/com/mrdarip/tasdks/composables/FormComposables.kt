package com.mrdarip.tasdks.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TaskFields(
    taskName: String = "test",
    taskEmoji: String = "🍀",
    taskComment: String = "comment test",
    onTaskNameChange: (String) -> Unit = {},
    onTaskEmojiChange: (String) -> Unit = {},
    onTaskCommentChange: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = taskName,
                onValueChange = { onTaskNameChange(it) },
                label = { Text("Name") },
                placeholder = { Text("Task name") },
                modifier = Modifier.weight(3f)
            )
            TextField(
                value = taskEmoji,
                onValueChange = { onTaskEmojiChange(it) },
                label = { Text("Emoji") },
                placeholder = { Text("😃") },
                modifier = Modifier.weight(1f)
            )
        }
        TextField(
            value = taskComment,
            onValueChange = { onTaskCommentChange(it) },
            label = { Text("Comment") },
            placeholder = { Text("Task comment") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}