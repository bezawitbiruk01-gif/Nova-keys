package com.novakeys.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.novakeys.R
import com.novakeys.storage.AppStorage

@Composable
fun NovaKeysNavigation(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    storage: AppStorage,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(R.string.app_name))
                        Text(
                            text = stringResource(R.string.developer_name),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                },
            )
        },
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
            storage = storage,
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
    storage: AppStorage,
) {
    when (screen) {
        AppScreen.Home -> HomeScreen(storage = storage, modifier = modifier)
        AppScreen.Library -> LibraryScreen(modifier)
        AppScreen.Settings -> SettingsScreen(modifier)
    }
}