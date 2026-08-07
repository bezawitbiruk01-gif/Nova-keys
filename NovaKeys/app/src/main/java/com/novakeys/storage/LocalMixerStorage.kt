package com.novakeys.storage

import android.content.Context
import android.content.SharedPreferences
import com.novakeys.mixer.MixerState

class LocalMixerStorage(
    context: Context,
) : MixerStorage {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun readMixerState(): MixerState {
        return MixerState(
            masterVolume = preferences.getFloat(MASTER_VOLUME_KEY, 0.80f),
            accompanimentVolume = preferences.getFloat(ACCOMPANIMENT_VOLUME_KEY, 0.75f),
            melodyVolume = preferences.getFloat(MELODY_VOLUME_KEY, 0.85f),
            bassVolume = preferences.getFloat(BASS_VOLUME_KEY, 0.80f),
            drumsVolume = preferences.getFloat(DRUMS_VOLUME_KEY, 0.80f),
            reverbSend = preferences.getFloat(REVERB_SEND_KEY, 0.20f),
            chorusSend = preferences.getFloat(CHORUS_SEND_KEY, 0.10f),
            eqLow = preferences.getFloat(EQ_LOW_KEY, 0.00f),
            eqMid = preferences.getFloat(EQ_MID_KEY, 0.00f),
            eqHigh = preferences.getFloat(EQ_HIGH_KEY, 0.00f),
            reverbEnabled = preferences.getBoolean(REVERB_ENABLED_KEY, true),
            chorusEnabled = preferences.getBoolean(CHORUS_ENABLED_KEY, true),
            eqEnabled = preferences.getBoolean(EQ_ENABLED_KEY, true),
        )
    }

    override fun writeMixerState(state: MixerState) {
        preferences.edit()
            .putFloat(MASTER_VOLUME_KEY, state.masterVolume)
            .putFloat(ACCOMPANIMENT_VOLUME_KEY, state.accompanimentVolume)
            .putFloat(MELODY_VOLUME_KEY, state.melodyVolume)
            .putFloat(BASS_VOLUME_KEY, state.bassVolume)
            .putFloat(DRUMS_VOLUME_KEY, state.drumsVolume)
            .putFloat(REVERB_SEND_KEY, state.reverbSend)
            .putFloat(CHORUS_SEND_KEY, state.chorusSend)
            .putFloat(EQ_LOW_KEY, state.eqLow)
            .putFloat(EQ_MID_KEY, state.eqMid)
            .putFloat(EQ_HIGH_KEY, state.eqHigh)
            .putBoolean(REVERB_ENABLED_KEY, state.reverbEnabled)
            .putBoolean(CHORUS_ENABLED_KEY, state.chorusEnabled)
            .putBoolean(EQ_ENABLED_KEY, state.eqEnabled)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "novakeys_mixer"
        const val MASTER_VOLUME_KEY = "master_volume"
        const val ACCOMPANIMENT_VOLUME_KEY = "accompaniment_volume"
        const val MELODY_VOLUME_KEY = "melody_volume"
        const val BASS_VOLUME_KEY = "bass_volume"
        const val DRUMS_VOLUME_KEY = "drums_volume"
        const val REVERB_SEND_KEY = "reverb_send"
        const val CHORUS_SEND_KEY = "chorus_send"
        const val EQ_LOW_KEY = "eq_low"
        const val EQ_MID_KEY = "eq_mid"
        const val EQ_HIGH_KEY = "eq_high"
        const val REVERB_ENABLED_KEY = "reverb_enabled"
        const val CHORUS_ENABLED_KEY = "chorus_enabled"
        const val EQ_ENABLED_KEY = "eq_enabled"
    }
}