package com.novakeys.sampler

data class SoundFontInfo(
    val id: String,
    val displayName: String,
    val description: String = "",
)

data class SamplerState(
    val engineReady: Boolean = false,
    val polyphonyLimit: Int = 128,
    val activeVoices: Int = 0,
    val loadedSoundFonts: List<SoundFontInfo> = emptyList(),
    val selectedSoundFontId: String? = null,
)
