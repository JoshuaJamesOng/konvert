package com.ongtonnesoup.konvert.currency.domain

import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class GetCurrentDataState @Inject constructor(
        @Named("local") private val local: ExchangeRepository,
        private val appState: AppState
) {

    fun load(): Single<DataState> {
        return getFromAppState()
                .switchIfEmpty(checkLocalStorage())
    }

    private fun getFromAppState(): Maybe<DataState> {
        return appState.updates()
                .firstOrError()
                .map { appState -> appState.dataState }
                .filter { dataState -> dataState != DataState.UNKNOWN }
    }

    private fun checkLocalStorage(): Single<DataState> {
        return local.getExchangeRates()
                .map { rates ->
                    if (rates.rates.isEmpty()) {
                        DataState.NO_DATA
                    } else {
                        DataState.CACHED_DATA
                    }
                }
    }


}
