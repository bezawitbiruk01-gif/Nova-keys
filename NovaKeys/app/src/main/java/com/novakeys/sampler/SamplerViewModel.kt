package com.novakeys.sampler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.novakeys.storage.LocalSamplerStorage
import kotlinx.coroutines.flow.StateFlow

class SamplerViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val storage = LocalSamplerStorage(application.applicationContext)
    private val engine = BasicSampleEngine()

    val state: StateFlow<SamplerState> = engine.state

    init {
        engine.loadDemoSoundFont()
        val restored = storage.readState()
        engine.setPolyphonyLimit(restored.polyphonyLimit)
    }

    fun loadDemoSoundFont() {
        engine.loadDemoSoundFont()
        storage.writeState(state.value)
    }

    fun setPolyphonyLimit(limit: Int) {
        engine.setPolyphonyLimit(limit)
        storage.writeState(state.value)
    }

    fun noteOn(midiNote: Int, velocity: Int = 100) {
        engine.noteOn(midiNote, velocity)
    }

    fun noteOff(midiNote: Int) {
        engine.noteOff(midiNote)
    }

    override fun onCleared() {
        storage.writeState(state.value)
        engine.release()
        super.onCleared()
    }
}
