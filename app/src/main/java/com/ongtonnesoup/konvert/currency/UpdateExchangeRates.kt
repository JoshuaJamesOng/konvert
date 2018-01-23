package com.ongtonnesoup.konvert.currency

import com.github.ajalt.timberkt.Timber
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class UpdateExchangeRates(private val network: ExchangeRepository,
                          private val local: ExchangeRepository) {

    fun getExchangeRates(): Completable {
        Timber.d { "Creating local observable on ${Thread.currentThread()}" }
        return network.getExchangeRates()
                .subscribeOn(Schedulers.io())
                .flatMapCompletable {
                    Timber.d { "Flat mapping network to local on ${Thread.currentThread()}}" }
                    local.putExchangeRates(it)
                }
    }

}