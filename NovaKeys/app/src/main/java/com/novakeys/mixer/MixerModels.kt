package com.novakeys.mixer

data class MixerState(
    val masterVolume: Float = 0.80f,
    val accompanimentVolume: Float = 0.75f,
    val melodyVolume: Float = 0.85f,
    val bassVolume: Float = 0.80f,
    val drumsVolume: Float = 0.80f,
    val reverbSend: Float = 0.20f,
    val chorusSend: Float = 0.10f,
    val eqLow: Float = 0.00f,
    val eqMid: Float = 0.00f,
    val eqHigh: Float = 0.00f,
    val reverbEnabled: Boolean = true,
    val chorusEnabled: Boolean = true,
    val eqEnabled: Boolean = true,
)