package com.ongtonnesoup.konvert.currency.domain

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.RefreshState
import com.ongtonnesoup.konvert.state.updateRefreshState
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Named

class UpdateExchangeRates @Inject constructor(
        @Named("network") private val network: ExchangeRepository,
        @Named("local") private val local: ExchangeRepository,
        private val appState: AppState
) {

    fun getExchangeRates(): Completable {
        Timber.d { "Creating local observable on ${Thread.currentThread()}" }
        return network.getExchangeRates()
                .doOnSubscribe { updateRefreshState(appState, RefreshState.REFRESHING) }
                .flatMapCompletable {
                    Timber.d { "Flat mapping network to local on ${Thread.currentThread()}}" }
                    local.putExchangeRates(it)
                }
    }

}
