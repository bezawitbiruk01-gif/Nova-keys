package com.novakeys.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.novakeys.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    BaseScreen(
        modifier = modifier,
        title = stringResource(R.string.home_title),
        message = stringResource(R.string.home_message),
    )
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    BaseScreen(
        modifier = modifier,
        title = stringResource(R.string.library_title),
        message = stringResource(R.string.library_message),
    )
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    BaseScreen(
        modifier = modifier,
        title = stringResource(R.string.settings_title),
        message = stringResource(R.string.settings_message),
    )
}

@Composable
private fun BaseScreen(
    modifier: Modifier,
    title: String,
    message: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}