package com.ongtonnesoup.konvert.currency

import com.ongtonnesoup.konvert.currency.domain.ExchangeRepository
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.DataState
import com.ongtonnesoup.konvert.state.updateDataState
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Named

class SaveExchangeRates @Inject constructor(
        @Named("local") private val local: ExchangeRepository,
        private val appState: AppState
) {

    fun save(exchangeRates: ExchangeRepository.ExchangeRates): Completable {
        return local.putExchangeRates(exchangeRates)
                .doOnComplete { updateDataState(appState, DataState.CACHED_DATA) }
    }
}