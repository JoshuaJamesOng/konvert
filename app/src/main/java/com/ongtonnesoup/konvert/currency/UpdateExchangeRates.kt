package com.ongtonnesoup.konvert.currency

import io.reactivex.Completable

class UpdateExchangeRates(private val network: ExchangeRepository,
                          private val local: ExchangeRepository) {

    fun getExchangeRates(): Completable {
        return network.getExchangeRates()
                .flatMapCompletable { local.putExchangeRates(it) }
    }

}