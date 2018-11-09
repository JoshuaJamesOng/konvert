package com.ongtonnesoup.konvert.currency.domain

import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import javax.inject.Inject
import javax.inject.Named

class GetCurrentDataState @Inject constructor(
        @Named("local") private val local: ExchangeRepository,
        private val appState: AppState
) {
    suspend fun load(): DataState {
        val appState = getFromAppState()
        return if (appState != DataState.UNKNOWN) {
            appState
        } else {
            checkLocalStorage()
        }
    }

    private fun getFromAppState() = appState.current().dataState

    private suspend fun checkLocalStorage(): DataState {
        return if (local.getExchangeRates().rates.isEmpty()) {
            DataState.NO_DATA
        } else {
            DataState.CACHED_DATA
        }
    }
}
