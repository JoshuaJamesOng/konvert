package com.ongtonnesoup.konvert.detection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ongtonnesoup.konvert.di.ApplicationComponent

class DetectionViewModelFactory(
        private val initialState: State?,
        private val component: ApplicationComponent
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetectionViewModel(initialState, component) as T
    }
}