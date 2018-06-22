package com.ongtonnesoup.konvert.currency

import com.github.ajalt.timberkt.Timber
import io.reactivex.Completable
import javax.inject.Inject

class UpdateExchangeRates @Inject constructor(
        private val getLatestExchangeRates: GetLatestExchangeRates,
        private val saveExchangeRates: SaveExchangeRates
) {

    fun getExchangeRates(): Completable {
        Timber.d { "Creating local observable on ${Thread.currentThread()}" }
        return getLatestExchangeRates.getExchangeRates()
                .flatMapCompletable(saveExchangeRates::save) // TODO Only save if valid response
    }

}
