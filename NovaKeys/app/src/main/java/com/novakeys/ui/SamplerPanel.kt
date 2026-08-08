package com.novakeys.ui

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novakeys.sampler.SamplerViewModel
import com.novakeys.sampler.SamplerViewModelFactory
import com.novakeys.sampler.SoundFontCatalog

@Composable
fun SamplerPanel(
    viewModel: SamplerViewModel = viewModel(
        factory = SamplerViewModelFactory(LocalContext.current.applicationContext as Application),
    ),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Sampler")
        Text(text = "Status: ${if (state.engineReady) "Ready" else "Starting"}")
        Text(text = "Active voices: ${state.activeVoices}/${state.polyphonyLimit}")
        Text(text = "SoundFonts loaded: ${state.loadedSoundFonts.size}")
        state.selectedSoundFontId?.let { selectedId ->
            state.loadedSoundFonts.firstOrNull { it.id == selectedId }?.let { soundFont ->
                Text(text = "Selected: ${soundFont.displayName}")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Built-in SoundFonts")
            SoundFontCatalog.builtInSoundFonts.forEach { soundFont ->
                Button(
                    onClick = { viewModel.selectSoundFont(soundFont.id) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = soundFont.displayName)
                }
                Text(text = soundFont.description)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::loadDemoSoundFont) {
                Text(text = "Load Demo")
            }
            Button(onClick = { viewModel.noteOn(60) }) {
                Text(text = "Test Note")
            }
            Button(onClick = { viewModel.noteOff(60) }) {
                Text(text = "Stop Note")
            }
        }

        Text(text = "Polyphony")
        Slider(
            value = state.polyphonyLimit.toFloat(),
            onValueChange = { viewModel.setPolyphonyLimit(it.toInt()) },
            valueRange = 1f..128f,
            steps = 126,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
