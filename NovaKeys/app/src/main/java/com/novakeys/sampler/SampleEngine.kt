package com.novakeys.sampler

import kotlinx.coroutines.flow.StateFlow

interface SampleEngine {
    val state: StateFlow<SamplerState>

    fun loadSoundFont(soundFont: SoundFontInfo)

    fun noteOn(midiNote: Int, velocity: Int = 100)

    fun noteOff(midiNote: Int)

    fun setPolyphonyLimit(limit: Int)

    fun release()
}
