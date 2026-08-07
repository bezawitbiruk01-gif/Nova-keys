package com.novakeys.storage

import android.content.Context
import android.content.SharedPreferences
import com.novakeys.ui.AppScreen

interface AppStorage {
    fun readSelectedScreen(): AppScreen

    fun writeSelectedScreen(screen: AppScreen)
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

    private companion object {
        const val PREFERENCES_NAME = "novakeys_preferences"
        const val SELECTED_SCREEN_KEY = "selected_screen"
    }
}