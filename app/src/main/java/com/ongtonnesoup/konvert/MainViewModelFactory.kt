package com.ongtonnesoup.konvert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.initialisation.CheckLocalRatesAvailable

class MainViewModelFactory(
        private val initialState: State?,
        private val checkLocalRatesAvailable: CheckLocalRatesAvailable,
        private val dispatchers: Dispatchers
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(initialState, checkLocalRatesAvailable, dispatchers) as T
    }
}