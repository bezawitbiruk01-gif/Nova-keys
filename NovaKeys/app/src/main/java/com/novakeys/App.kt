package com.novakeys

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novakeys.storage.LocalAppStorage
import com.novakeys.ui.NovaKeysNavigation
import com.novakeys.ui.NovaKeysTheme
import com.novakeys.ui.viewmodel.AppViewModel
import com.novakeys.ui.viewmodel.AppViewModelFactory

@Composable
fun App() {
    val context = LocalContext.current
    val storage = remember(context) { LocalAppStorage(context) }
    val appViewModel: AppViewModel = viewModel(
        factory = AppViewModelFactory(storage),
    )
    val state by appViewModel.uiState.collectAsStateWithLifecycle()

    NovaKeysTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NovaKeysNavigation(
                currentScreen = state.currentScreen,
                onScreenSelected = appViewModel::selectScreen,
            )
        }
    }
}