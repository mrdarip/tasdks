package com.mrdarip.tasdks.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainMenu(){
    Scaffold{
        BodyContent()
    }
}

@Composable
fun BodyContent(){
    Column {
        Text(text = "hola")
        Button(onClick = { /*TODO*/ }) {
            Text(text = "peo")
        }
    }
}

@Preview
@Composable
fun DefaultPreview(){
    MainMenu()
}