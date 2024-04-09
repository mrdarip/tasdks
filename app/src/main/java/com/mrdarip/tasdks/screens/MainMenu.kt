package com.mrdarip.tasdks.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrdarip.tasdks.R
import com.mrdarip.tasdks.data.entity.Playlist

@Composable
fun MainMenu() {
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

        for (i in 1..10) {
            SquarePlaylist(name = "hola"+i.toString(), drawable = R.drawable.ic_launcher_foreground)
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MainMenu()
}

@Composable
fun SquarePlaylist(name: String, @DrawableRes drawable: Int) {
    Column(verticalArrangement = Arrangement.SpaceBetween) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = "imagen"
        )
        Text(text = "Hola")
    }
}

@Composable
fun PlaylistsDisplay(title:String,playlist:List<Playlist>){

}