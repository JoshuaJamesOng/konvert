package com.ongtonnesoup.konvert.detection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ongtonnesoup.konvert.detection.di.DetectionComponent
import javax.inject.Inject
import javax.inject.Provider

class DetectionViewModelFactory(private val component: DetectionComponent) : ViewModelProvider.Factory {

    @Inject
    lateinit var viewModelProvider: Provider<DetectionViewModel>

    private val viewModel by lazy {
        component.inject(this)
        viewModelProvider.get()
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>) = viewModel as T

}
