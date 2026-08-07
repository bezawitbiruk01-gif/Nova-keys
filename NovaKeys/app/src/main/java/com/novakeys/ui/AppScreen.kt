package com.novakeys.ui

import androidx.annotation.StringRes
import com.novakeys.R

enum class AppScreen(
    @StringRes val labelResId: Int,
) {
    Home(R.string.screen_home),
    Library(R.string.screen_library),
    Settings(R.string.screen_settings),
}