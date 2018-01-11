package com.ongtonnesoup.konvert.currency

import com.github.ajalt.timberkt.Timber
import io.reactivex.Completable

class UpdateExchangeRates(private val network: ExchangeRepository,
                          private val local: ExchangeRepository) {

    fun getExchangeRates(): Completable {
        Timber.d { "Creating local observable" }
        return network.getExchangeRates()
                .flatMapCompletable {
                    Timber.d { "Flat mapping network to local" }
                    local.putExchangeRates(it)
                }
    }

}