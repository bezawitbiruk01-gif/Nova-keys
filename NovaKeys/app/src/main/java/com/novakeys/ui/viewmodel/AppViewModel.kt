package com.novakeys.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.novakeys.storage.AppStorage
import com.novakeys.ui.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppUiState(
    val currentScreen: AppScreen = AppScreen.Home,
)

class AppViewModel(
    private val storage: AppStorage,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        AppUiState(currentScreen = storage.readSelectedScreen()),
    )
    val uiState: StateFlow<AppUiState> = mutableUiState.asStateFlow()

    fun selectScreen(screen: AppScreen) {
        if (mutableUiState.value.currentScreen == screen) {
            return
        }
        storage.writeSelectedScreen(screen)
        mutableUiState.value = mutableUiState.value.copy(currentScreen = screen)
    }
}

class AppViewModelFactory(
    private val storage: AppStorage,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AppViewModel::class.java)) {
            "Unsupported ViewModel type: ${modelClass.name}"
        }
        return AppViewModel(storage) as T
    }
}