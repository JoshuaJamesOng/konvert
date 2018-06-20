package com.ongtonnesoup.konvert.currency.domain

import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.Schedulers
import io.reactivex.Completable

class UpdateExchangeRates(private val network: ExchangeRepository,
                          private val local: ExchangeRepository,
                          private val schedulers: Schedulers) {

    fun getExchangeRates(): Completable {
        Timber.d { "Creating local observable on ${Thread.currentThread()}" }
        return network.getExchangeRates()
                .subscribeOn(schedulers.getWorkerScheduler())
                .flatMapCompletable {
                    Timber.d { "Flat mapping network to local on ${Thread.currentThread()}}" }
                    local.putExchangeRates(it)
                }
    }

}
