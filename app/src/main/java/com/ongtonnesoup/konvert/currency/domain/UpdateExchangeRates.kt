package com.ongtonnesoup.konvert.currency.domain

import com.github.ajalt.timberkt.Timber
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Named

class UpdateExchangeRates @Inject constructor(
        @Named("network") private val network: ExchangeRepository,
        @Named("local") private val local: ExchangeRepository
) {

    fun getExchangeRates(): Completable {
        Timber.d { "Creating local observable on ${Thread.currentThread()}" }
        return network.getExchangeRates()
                .flatMapCompletable {
                    Timber.d { "Flat mapping network to local on ${Thread.currentThread()}}" }
                    local.putExchangeRates(it)
                }
    }

}
