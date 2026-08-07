package com.novakeys.storage

import android.content.Context
import android.content.SharedPreferences
import com.novakeys.keyboard.PerformanceState
import com.novakeys.ui.AppScreen

interface AppStorage {
    fun readSelectedScreen(): AppScreen

    fun writeSelectedScreen(screen: AppScreen)

    fun readPerformanceState(): PerformanceState

    fun writePerformanceState(state: PerformanceState)

    fun readRegistration(slot: Int): PerformanceState?

    fun writeRegistration(slot: Int, state: PerformanceState)

    fun readSelectedRegistration(): Int

    fun writeSelectedRegistration(slot: Int)
}

class LocalAppStorage(
    context: Context,
) : AppStorage {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun readSelectedScreen(): AppScreen {
        val savedName = preferences.getString(SELECTED_SCREEN_KEY, null)
        return AppScreen.entries.firstOrNull { it.name == savedName } ?: AppScreen.Home
    }

    override fun writeSelectedScreen(screen: AppScreen) {
        preferences.edit().putString(SELECTED_SCREEN_KEY, screen.name).apply()
    }

    override fun readPerformanceState(): PerformanceState {
        return PerformanceState(
            splitEnabled = preferences.getBoolean(PERFORMANCE_SPLIT_KEY, false),
            splitPoint = preferences.getInt(PERFORMANCE_SPLIT_POINT_KEY, 60),
            layerEnabled = preferences.getBoolean(PERFORMANCE_LAYER_KEY, false),
            transpose = preferences.getInt(PERFORMANCE_TRANSPOSE_KEY, 0),
            sustainEnabled = preferences.getBoolean(PERFORMANCE_SUSTAIN_KEY, false),
        )
    }

    override fun writePerformanceState(state: PerformanceState) {
        preferences.edit()
            .putBoolean(PERFORMANCE_SPLIT_KEY, state.splitEnabled)
            .putInt(PERFORMANCE_SPLIT_POINT_KEY, state.splitPoint)
            .putBoolean(PERFORMANCE_LAYER_KEY, state.layerEnabled)
            .putInt(PERFORMANCE_TRANSPOSE_KEY, state.transpose)
            .putBoolean(PERFORMANCE_SUSTAIN_KEY, state.sustainEnabled)
            .apply()
    }

    override fun readRegistration(slot: Int): PerformanceState? {
        if (!preferences.contains(registrationKey(slot, REGISTRATION_SPLIT_SUFFIX))) {
            return null
        }
        return PerformanceState(
            splitEnabled = preferences.getBoolean(
                registrationKey(slot, REGISTRATION_SPLIT_SUFFIX),
                false,
            ),
            splitPoint = preferences.getInt(
                registrationKey(slot, REGISTRATION_SPLIT_POINT_SUFFIX),
                60,
            ),
            layerEnabled = preferences.getBoolean(
                registrationKey(slot, REGISTRATION_LAYER_SUFFIX),
                false,
            ),
            transpose = preferences.getInt(
                registrationKey(slot, REGISTRATION_TRANSPOSE_SUFFIX),
                0,
            ),
            sustainEnabled = preferences.getBoolean(
                registrationKey(slot, REGISTRATION_SUSTAIN_SUFFIX),
                false,
            ),
        )
    }

    override fun writeRegistration(slot: Int, state: PerformanceState) {
        preferences.edit()
            .putBoolean(registrationKey(slot, REGISTRATION_SPLIT_SUFFIX), state.splitEnabled)
            .putInt(registrationKey(slot, REGISTRATION_SPLIT_POINT_SUFFIX), state.splitPoint)
            .putBoolean(registrationKey(slot, REGISTRATION_LAYER_SUFFIX), state.layerEnabled)
            .putInt(registrationKey(slot, REGISTRATION_TRANSPOSE_SUFFIX), state.transpose)
            .putBoolean(registrationKey(slot, REGISTRATION_SUSTAIN_SUFFIX), state.sustainEnabled)
            .apply()
    }

    override fun readSelectedRegistration(): Int {
        return preferences.getInt(SELECTED_REGISTRATION_KEY, 1).coerceIn(1, 3)
    }

    override fun writeSelectedRegistration(slot: Int) {
        preferences.edit()
            .putInt(SELECTED_REGISTRATION_KEY, slot.coerceIn(1, 3))
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "novakeys_preferences"
        const val SELECTED_SCREEN_KEY = "selected_screen"
        const val SELECTED_REGISTRATION_KEY = "selected_registration"
        const val PERFORMANCE_SPLIT_KEY = "performance_split"
        const val PERFORMANCE_SPLIT_POINT_KEY = "performance_split_point"
        const val PERFORMANCE_LAYER_KEY = "performance_layer"
        const val PERFORMANCE_TRANSPOSE_KEY = "performance_transpose"
        const val PERFORMANCE_SUSTAIN_KEY = "performance_sustain"
        const val REGISTRATION_PREFIX = "registration_"
        const val REGISTRATION_SPLIT_SUFFIX = "split"
        const val REGISTRATION_SPLIT_POINT_SUFFIX = "split_point"
        const val REGISTRATION_LAYER_SUFFIX = "layer"
        const val REGISTRATION_TRANSPOSE_SUFFIX = "transpose"
        const val REGISTRATION_SUSTAIN_SUFFIX = "sustain"

        fun registrationKey(slot: Int, suffix: String): String {
            return "$REGISTRATION_PREFIX${slot.coerceIn(1, 3)}_$suffix"
        }
    }
}