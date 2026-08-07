package com.novakeys.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NovaKeysLightColors = lightColorScheme()
private val NovaKeysDarkColors = darkColorScheme()

@Composable
fun NovaKeysTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) NovaKeysDarkColors else NovaKeysLightColors,
        content = content,
    )
}