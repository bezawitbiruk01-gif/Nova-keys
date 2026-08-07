package com.novakeys.storage

import com.novakeys.mixer.MixerState

interface MixerStorage {
    fun readMixerState(): MixerState

    fun writeMixerState(state: MixerState)
}