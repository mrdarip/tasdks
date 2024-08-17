package com.mrdarip.tasdks.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NotFoundScreen() {
    NotFoundBodyContent()
}

@Composable
private fun NotFoundBodyContent() {
    Column {
        Text(
            text = "Screen not found, sorry ??",
            textAlign = TextAlign.Center
        ) //todo: implement report button
    }
}