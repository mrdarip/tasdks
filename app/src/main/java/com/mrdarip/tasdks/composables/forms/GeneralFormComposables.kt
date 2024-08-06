package com.mrdarip.tasdks.composables.forms

import android.icu.text.BreakIterator
import android.util.Log
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale

@Composable
fun NumberInput(
    modifier: Modifier = Modifier,
    value: Int,
    onValidValueChange: (Int) -> Unit,
    label: String,
    placeholder: String,
    suffix: @Composable (() -> Unit)? = null
) {
    var displayedValue by remember {
        mutableStateOf(value.toString())
    }

    LaunchedEffect(value) {
        displayedValue = value.toString()
    }

    TextField(
        value = displayedValue,
        onValueChange = {
            displayedValue = it
            if (displayedValue.isNotBlank() || displayedValue.toIntOrNull() != null) {
                Log.i("NumberInput", "onValidValueChange: ${it.toIntOrNull() ?: 0}")
                onValidValueChange(it.toIntOrNull() ?: 0)
            }
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        suffix = suffix,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = displayedValue.toIntOrNull() == null && displayedValue.isNotBlank(),
        modifier = modifier
    )
    //would be cool to implement evaluating its value if an expression like 1+1 is prompted
}

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean = true
) {
    var displayedValue by remember {
        mutableStateOf(value)
    }

    LaunchedEffect(value) {
        displayedValue = value
    }

    TextField(
        value = displayedValue,
        onValueChange = {
            displayedValue = it
            //its always valid so theres no if statement
            onValueChange(it)
        },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier,
        singleLine = singleLine
    )
}

//todo: move to utils?
fun capitalized(text: String): String {
    return text.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }
}

fun isValidEmoji(emoji: String): Boolean {
    return getLength(emoji) <= 1
}

fun getLength(emoji: String?): Int {
    val it: BreakIterator = BreakIterator.getCharacterInstance()
    it.setText(emoji)
    var count = 0
    while (it.next() != BreakIterator.DONE) {
        count++
    }
    return count
}