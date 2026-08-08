package com.novakeys.mixer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MixerViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MixerViewModel::class.java)) {
            "Unsupported ViewModel type: ${modelClass.name}"
        }
        return MixerViewModel(application) as T
    }
}
