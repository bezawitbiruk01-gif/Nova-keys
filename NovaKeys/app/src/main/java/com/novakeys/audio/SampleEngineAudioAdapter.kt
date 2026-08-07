package com.novakeys.audio

import com.novakeys.sampler.BasicSampleEngine
import com.novakeys.sampler.SampleEngine

class SampleEngineAudioAdapter(
    private val engine: SampleEngine = BasicSampleEngine(),
) : AudioEngine {
    init {
        engine.loadSoundFont(
            com.novakeys.sampler.SoundFontInfo(
                id = "demo-sine",
                displayName = "Demo Sine Set",
                description = "Offline placeholder sound source",
            ),
        )
    }

    override fun noteOn(midiNote: Int) {
        engine.noteOn(midiNote)
    }

    override fun noteOff(midiNote: Int) {
        engine.noteOff(midiNote)
    }

    override fun setSustain(enabled: Boolean) {
        engine.setSustain(enabled)
    }

    override fun setMasterVolume(volume: Float) {
        engine.setMasterVolume(volume)
    }

    override fun release() {
        engine.release()
    }
}
