package com.ongtonnesoup.konvert.currency

import io.reactivex.Completable
import io.reactivex.Single

interface ExchangeRepository {

    fun getExchangeRates(): Single<ExchangeRates>

    fun putExchangeRates(rates: ExchangeRates): Completable

    data class ExchangeRates(
            val rates: List<ExchangeRate>
    )

    data class ExchangeRate(
            val currency: String,
            val rate: Double
    )

}