package com.novakeys.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.novakeys.R

@Composable
fun NovaKeysNavigation(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                AppScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { onScreenSelected(screen) },
                        icon = {
                            Icon(
                                imageVector = screenIcon(screen),
                                contentDescription = stringResource(screen.labelResId),
                            )
                        },
                        label = { Text(stringResource(screen.labelResId)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        ScreenContent(
            screen = currentScreen,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

private fun screenIcon(screen: AppScreen) = when (screen) {
    AppScreen.Home -> Icons.Default.Home
    AppScreen.Library -> Icons.Default.LibraryMusic
    AppScreen.Settings -> Icons.Default.Settings
}

@Composable
private fun ScreenContent(
    screen: AppScreen,
    modifier: Modifier = Modifier,
) {
    when (screen) {
        AppScreen.Home -> HomeScreen(modifier)
        AppScreen.Library -> LibraryScreen(modifier)
        AppScreen.Settings -> SettingsScreen(modifier)
    }
}