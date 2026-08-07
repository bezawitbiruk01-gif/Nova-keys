package com.novakeys.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
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
import com.novakeys.keyboard.KeyboardState
import com.novakeys.keyboard.KeyboardViewModel
import com.novakeys.keyboard.KeyboardViewModelFactory
import com.novakeys.storage.AppStorage

@Composable
fun HomeScreen(
    storage: AppStorage,
    modifier: Modifier = Modifier,
) {
    val keyboardViewModel: KeyboardViewModel = viewModel(
        factory = KeyboardViewModelFactory(storage),
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
        PerformanceControls(
            state = keyboardState,
            onSplitChanged = keyboardViewModel::setSplitEnabled,
            onLayerChanged = keyboardViewModel::setLayerEnabled,
            onSustainChanged = keyboardViewModel::setSustainEnabled,
            onTransposeChanged = keyboardViewModel::transposeBy,
            onSplitPointChanged = keyboardViewModel::setSplitPoint,
            onSaveRegistration = keyboardViewModel::saveRegistration,
            onLoadRegistration = keyboardViewModel::loadRegistration,
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
private fun PerformanceControls(
    state: KeyboardState,
    onSplitChanged: (Boolean) -> Unit,
    onLayerChanged: (Boolean) -> Unit,
    onSustainChanged: (Boolean) -> Unit,
    onTransposeChanged: (Int) -> Unit,
    onSplitPointChanged: (Int) -> Unit,
    onSaveRegistration: (Int) -> Unit,
    onLoadRegistration: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FilterChip(
                selected = state.performance.splitEnabled,
                onClick = { onSplitChanged(!state.performance.splitEnabled) },
                label = { Text("Split") },
            )
            FilterChip(
                selected = state.performance.layerEnabled,
                onClick = { onLayerChanged(!state.performance.layerEnabled) },
                label = { Text("Layer") },
            )
            FilterChip(
                selected = state.performance.sustainEnabled,
                onClick = { onSustainChanged(!state.performance.sustainEnabled) },
                label = { Text("Sustain") },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Transpose ${state.performance.transpose}",
                style = MaterialTheme.typography.labelMedium,
            )
            Button(onClick = { onTransposeChanged(-1) }) { Text("−") }
            Button(onClick = { onTransposeChanged(1) }) { Text("+") }
            Text(
                text = "Split ${state.performance.splitPoint}",
                style = MaterialTheme.typography.labelMedium,
            )
            Button(onClick = { onSplitPointChanged(state.performance.splitPoint - 1) }) {
                Text("−")
            }
            Button(onClick = { onSplitPointChanged(state.performance.splitPoint + 1) }) {
                Text("+")
            }
        }
        Text(
            text = "Chord: ${state.chordName}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            (1..3).forEach { slot ->
                Button(
                    onClick = { onLoadRegistration(slot) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Load $slot")
                }
                Button(
                    onClick = { onSaveRegistration(slot) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Save $slot")
                }
            }
        }
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