package com.novakeys.storage

import com.novakeys.sampler.SamplerState

interface SamplerStorage {
    fun readState(): SamplerState
    fun writeState(state: SamplerState)
}
