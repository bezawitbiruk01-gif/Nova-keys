package com.novakeys.mixer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.novakeys.storage.LocalMixerStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MixerViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val storage = LocalMixerStorage(application.applicationContext)

    private val mutableState = MutableStateFlow(storage.readMixerState())
    val state: StateFlow<MixerState> = mutableState.asStateFlow()

    fun setMasterVolume(value: Float) = update { it.copy(masterVolume = value.coerceIn(0f, 1f)) }
    fun setAccompanimentVolume(value: Float) = update { it.copy(accompanimentVolume = value.coerceIn(0f, 1f)) }
    fun setMelodyVolume(value: Float) = update { it.copy(melodyVolume = value.coerceIn(0f, 1f)) }
    fun setBassVolume(value: Float) = update { it.copy(bassVolume = value.coerceIn(0f, 1f)) }
    fun setDrumsVolume(value: Float) = update { it.copy(drumsVolume = value.coerceIn(0f, 1f)) }
    fun setReverbSend(value: Float) = update { it.copy(reverbSend = value.coerceIn(0f, 1f)) }
    fun setChorusSend(value: Float) = update { it.copy(chorusSend = value.coerceIn(0f, 1f)) }
    fun setEqLow(value: Float) = update { it.copy(eqLow = value.coerceIn(-1f, 1f)) }
    fun setEqMid(value: Float) = update { it.copy(eqMid = value.coerceIn(-1f, 1f)) }
    fun setEqHigh(value: Float) = update { it.copy(eqHigh = value.coerceIn(-1f, 1f)) }

    fun toggleReverbEnabled() = update { it.copy(reverbEnabled = !it.reverbEnabled) }
    fun toggleChorusEnabled() = update { it.copy(chorusEnabled = !it.chorusEnabled) }
    fun toggleEqEnabled() = update { it.copy(eqEnabled = !it.eqEnabled) }

    private fun update(reducer: (MixerState) -> MixerState) {
        val next = reducer(mutableState.value)
        mutableState.value = next
        viewModelScope.launch {
            storage.writeMixerState(next)
        }
    }
}