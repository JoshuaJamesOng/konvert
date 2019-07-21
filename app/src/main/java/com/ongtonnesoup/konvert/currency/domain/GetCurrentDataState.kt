package com.ongtonnesoup.konvert.currency.domain

import arrow.core.Try
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import javax.inject.Inject
import javax.inject.Named

class GetCurrentDataState @Inject constructor(
        @Named("local") private val local: ExchangeRepository,
        private val appState: AppState
) {
    suspend fun load(): DataState {
        val appState = getFromAppState() // TODO if we keep this state objec then it should watch the DB
        return if (appState != DataState.UNKNOWN) {
            appState
        } else {
            checkLocalStorage()
        }
    }

    private fun getFromAppState() = appState.current().dataState

    private suspend fun checkLocalStorage(): DataState {
        suspend fun isRatesInLocalStorage() = local.getExchangeRates()
                .map { t -> !t.rates.isEmpty() }
                .flatMap { isRates ->
                    if (isRates) {
                        Try.just(true)
                    } else {
                        Try.raiseError(ExchangeRepository.NoDataException())
                    }
                }

        return isRatesInLocalStorage()
                .fold(
                        ifSuccess = { DataState.CACHED_DATA },
                        ifFailure = { DataState.NO_DATA }
                )
    }
}
