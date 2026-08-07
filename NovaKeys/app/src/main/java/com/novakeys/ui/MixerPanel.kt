package com.novakeys.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novakeys.mixer.MixerViewModel

@Composable
fun MixerPanel(
    viewModel: MixerViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Mixer")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VolumeControl("Master", state.masterVolume, viewModel::setMasterVolume)
            VolumeControl("Accomp", state.accompanimentVolume, viewModel::setAccompanimentVolume)
            VolumeControl("Melody", state.melodyVolume, viewModel::setMelodyVolume)
            VolumeControl("Bass", state.bassVolume, viewModel::setBassVolume)
            VolumeControl("Drums", state.drumsVolume, viewModel::setDrumsVolume)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = state.reverbEnabled,
                onClick = viewModel::toggleReverbEnabled,
                label = { Text("Reverb") },
            )
            FilterChip(
                selected = state.chorusEnabled,
                onClick = viewModel::toggleChorusEnabled,
                label = { Text("Chorus") },
            )
            FilterChip(
                selected = state.eqEnabled,
                onClick = viewModel::toggleEqEnabled,
                label = { Text("EQ") },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VolumeControl("Rev Send", state.reverbSend, viewModel::setReverbSend)
            VolumeControl("Cho Send", state.chorusSend, viewModel::setChorusSend)
            VolumeControl("Low", state.eqLow, viewModel::setEqLow, -1f..1f)
            VolumeControl("Mid", state.eqMid, viewModel::setEqMid, -1f..1f)
            VolumeControl("High", state.eqHigh, viewModel::setEqHigh, -1f..1f)
        }
    }
}

@Composable
private fun VolumeControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Column(
        modifier = Modifier.padding(end = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(label)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth(fraction = 0.18f),
        )
    }
}