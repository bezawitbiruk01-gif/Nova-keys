package com.novakeys.sampler

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class SamplerViewModel : ViewModel() {
    private val engine = BasicSampleEngine()

    val state: StateFlow<SamplerState> = engine.state

    init {
        engine.loadDemoSoundFont()
    }

    fun loadDemoSoundFont() {
        engine.loadDemoSoundFont()
    }

    fun setPolyphonyLimit(limit: Int) {
        engine.setPolyphonyLimit(limit)
    }

    fun noteOn(midiNote: Int, velocity: Int = 100) {
        engine.noteOn(midiNote, velocity)
    }

    fun noteOff(midiNote: Int) {
        engine.noteOff(midiNote)
    }

    override fun onCleared() {
        engine.release()
        super.onCleared()
    }
}
