package com.mrdarip.tasdks.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrdarip.tasdks.R
import com.mrdarip.tasdks.data.Repository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(navController: NavController,repository: Repository) {
    Text(
        text = "Hello World!"
    )
    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            Text("Top app bar")
        })
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
            title = "Hola mundo"
        )

    }
}


@Composable
fun SquarePlaylist(name: String, @DrawableRes drawable: Int) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .clickable(onClick = {/*TODO*/ })
            .padding(24.dp)
    ) {
        Image(
            painter = painterResource(id = drawable), contentDescription = "imagen"
        )
        Text(text = name)
    }
}

@Composable
fun PlaylistsDisplay(title: String) {

    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        /*for (playlist in playlists) {
            SquarePlaylist(name = playlist.name, drawable = R.drawable.ic_launcher_foreground)
        }*/
    }

}