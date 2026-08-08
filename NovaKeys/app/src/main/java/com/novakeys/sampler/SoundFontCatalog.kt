package com.novakeys.sampler

object SoundFontCatalog {
    val builtInSoundFonts: List<SoundFontInfo> = listOf(
        SoundFontInfo(
            id = "demo-sine",
            displayName = "Demo Sine Set",
            description = "Lightweight offline placeholder set for quick testing.",
        ),
        SoundFontInfo(
            id = "warm-piano",
            displayName = "Warm Piano",
            description = "Soft piano-style placeholder voice for melodic playing.",
        ),
        SoundFontInfo(
            id = "bright-keys",
            displayName = "Bright Keys",
            description = "Brighter keyboard color for lead and pop-style parts.",
        ),
        SoundFontInfo(
            id = "organ-ensemble",
            displayName = "Organ Ensemble",
            description = "Organ-like placeholder timbre for sustained chords.",
        ),
    )

    fun defaultSoundFontId(): String = builtInSoundFonts.first().id
}
