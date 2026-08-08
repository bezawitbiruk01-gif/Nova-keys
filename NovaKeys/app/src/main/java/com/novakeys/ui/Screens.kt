package com.novakeys.ui

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novakeys.R
import com.novakeys.keyboard.KeyboardSurface
import com.novakeys.keyboard.KeyboardState
import com.novakeys.keyboard.KeyboardViewModel
import com.novakeys.keyboard.KeyboardViewModelFactory
import com.novakeys.mixer.MixerViewModel
import com.novakeys.mixer.MixerViewModelFactory
import com.novakeys.storage.AppStorage

@Composable
fun HomeScreen(storage: AppStorage, modifier: Modifier = Modifier) {
    val keyboardViewModel: KeyboardViewModel = viewModel(factory = KeyboardViewModelFactory(storage))
    val mixerViewModel: MixerViewModel = viewModel(
        factory = MixerViewModelFactory(LocalContext.current.applicationContext as Application),
    )
    val keyboardState by keyboardViewModel.state.collectAsStateWithLifecycle()
    val mixerState by mixerViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(mixerState.masterVolume) {
        keyboardViewModel.setMasterVolume(mixerState.masterVolume)
    }

    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
        Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineMedium)
        Text(
            stringResource(R.string.home_message),
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            stringResource(R.string.developer_name),
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
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
        MixerPanel(mixerViewModel)
        KeyboardSurface(
            state = keyboardState,
            onNoteOn = keyboardViewModel::noteOn,
            onNoteOff = keyboardViewModel::noteOff,
            modifier = Modifier.weight(1f).fillMaxWidth(),
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
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(state.performance.splitEnabled, { onSplitChanged(!state.performance.splitEnabled) }, label = { Text("Split") })
            FilterChip(state.performance.layerEnabled, { onLayerChanged(!state.performance.layerEnabled) }, label = { Text("Layer") })
            FilterChip(state.performance.sustainEnabled, { onSustainChanged(!state.performance.sustainEnabled) }, label = { Text("Sustain") })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Transpose ${state.performance.transpose}", style = MaterialTheme.typography.labelMedium)
            Button(onClick = { onTransposeChanged(-1) }) { Text("−") }
            Button(onClick = { onTransposeChanged(1) }) { Text("+") }
            Text("Split ${state.performance.splitPoint}", style = MaterialTheme.typography.labelMedium)
            Button(onClick = { onSplitPointChanged(state.performance.splitPoint - 1) }) { Text("−") }
            Button(onClick = { onSplitPointChanged(state.performance.splitPoint + 1) }) { Text("+") }
        }
        Text("Chord: ${state.chordName}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            RegistrationButtonRow("Load 1", { onLoadRegistration(1) }, Modifier.weight(1f))
            RegistrationButtonRow("Save 1", { onSaveRegistration(1) }, Modifier.weight(1f))
            RegistrationButtonRow("Load 2", { onLoadRegistration(2) }, Modifier.weight(1f))
            RegistrationButtonRow("Save 2", { onSaveRegistration(2) }, Modifier.weight(1f))
            RegistrationButtonRow("Load 3", { onLoadRegistration(3) }, Modifier.weight(1f))
            RegistrationButtonRow("Save 3", { onSaveRegistration(3) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun RowScope.RegistrationButtonRow(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier) { Text(label) }
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        BaseScreen(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.library_title),
            message = stringResource(R.string.library_message),
        )
        SamplerPanel()
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) = BaseScreen(
    modifier = modifier,
    title = stringResource(R.string.settings_title),
    message = stringResource(R.string.settings_message),
)

@Composable
private fun BaseScreen(modifier: Modifier, title: String, message: String) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(message, modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(R.string.developer_name),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
