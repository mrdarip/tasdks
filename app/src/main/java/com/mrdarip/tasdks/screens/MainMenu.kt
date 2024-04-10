package com.mrdarip.tasdks.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrdarip.tasdks.R
import com.mrdarip.tasdks.data.entity.Playlist

@Composable
fun MainMenu(navController: NavController) {
    Scaffold(topBar = {
        Text(text = "hello")
    }, bottomBar = {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Bottom app bar",
            )
        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = { }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BodyContent()
        }
    }
}

@Composable
fun BodyContent() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        PlaylistsDisplay(
            title = "Hola mundo",
            playlists = listOf(Playlist(0, "test1"), Playlist(1, "test2"))
        )

    }
}


@Composable
fun SquarePlaylist(name: String, @DrawableRes drawable: Int) {
    Column(verticalArrangement = Arrangement.SpaceBetween) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = "imagen"
        )
        Text(text = name)
    }
}

@Composable
fun PlaylistsDisplay(title: String, playlists: List<Playlist>) {
    Row {
        for (playlist in playlists) {
            SquarePlaylist(name = playlist.name, drawable = R.drawable.ic_launcher_foreground)
        }
    }

}