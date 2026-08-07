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
        state.loadedSoundFonts.lastOrNull()?.let { soundFont ->
            Text(text = "Selected: ${soundFont.displayName}")
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
