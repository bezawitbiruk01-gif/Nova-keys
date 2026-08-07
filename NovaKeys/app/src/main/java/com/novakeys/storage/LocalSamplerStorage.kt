package com.novakeys.storage

import android.content.Context
import android.content.SharedPreferences
import com.novakeys.sampler.SamplerState

class LocalSamplerStorage(
    context: Context,
) : SamplerStorage {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun readState(): SamplerState {
        return SamplerState(
            engineReady = preferences.getBoolean(ENGINE_READY_KEY, false),
            polyphonyLimit = preferences.getInt(POLYPHONY_LIMIT_KEY, 128),
            activeVoices = 0,
            loadedSoundFonts = emptyList(),
            selectedSoundFontId = preferences.getString(SELECTED_SOUNDFONT_ID_KEY, null),
        )
    }

    override fun writeState(state: SamplerState) {
        preferences.edit()
            .putBoolean(ENGINE_READY_KEY, state.engineReady)
            .putInt(POLYPHONY_LIMIT_KEY, state.polyphonyLimit)
            .putString(SELECTED_SOUNDFONT_ID_KEY, state.selectedSoundFontId)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "novakeys_sampler"
        const val ENGINE_READY_KEY = "engine_ready"
        const val POLYPHONY_LIMIT_KEY = "polyphony_limit"
        const val SELECTED_SOUNDFONT_ID_KEY = "selected_soundfont_id"
    }
}
