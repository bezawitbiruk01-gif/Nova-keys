package com.novakeys.sampler

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SamplerViewModelFactory(
    private val application: Application,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SamplerViewModel::class.java)) {
            "Unsupported ViewModel type: ${modelClass.name}"
        }
        return SamplerViewModel(application) as T
    }
}
