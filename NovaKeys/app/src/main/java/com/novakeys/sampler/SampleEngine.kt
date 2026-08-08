package com.novakeys.sampler

import kotlinx.coroutines.flow.StateFlow

interface SampleEngine {
    val state: StateFlow<SamplerState>

    fun loadSoundFont(soundFont: SoundFontInfo)

    fun selectSoundFont(soundFontId: String)

    fun noteOn(midiNote: Int, velocity: Int = 100)

    fun noteOff(midiNote: Int)

    fun setPolyphonyLimit(limit: Int)

    fun setSustain(enabled: Boolean)

    fun setMasterVolume(volume: Float)

    fun release()
}
