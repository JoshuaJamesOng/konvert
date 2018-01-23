package com.ongtonnesoup.konvert.currency.domain

import io.reactivex.Completable
import io.reactivex.Single

interface ExchangeRepository {

    companion object {
        val NO_DATA = ExchangeRates(emptyList())
    }

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
