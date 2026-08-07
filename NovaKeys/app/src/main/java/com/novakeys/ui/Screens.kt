package com.novakeys.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novakeys.R
import com.novakeys.keyboard.KeyboardSurface
import com.novakeys.keyboard.KeyboardViewModel
import com.novakeys.keyboard.KeyboardViewModelFactory

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val keyboardViewModel: KeyboardViewModel = viewModel(
        factory = KeyboardViewModelFactory(),
    )
    val keyboardState by keyboardViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.home_message),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
        KeyboardSurface(
            state = keyboardState,
            onNoteOn = keyboardViewModel::noteOn,
            onNoteOff = keyboardViewModel::noteOff,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }
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