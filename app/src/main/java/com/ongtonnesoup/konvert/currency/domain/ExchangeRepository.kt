package com.ongtonnesoup.konvert.currency.domain

import arrow.core.Try

interface ExchangeRepository {

    class NoDataException : Exception()

    suspend fun getExchangeRates(): Try<ExchangeRates>

    suspend fun putExchangeRates(rates: ExchangeRates)

    data class ExchangeRates(
        val rates: List<ExchangeRate>
    )

    data class ExchangeRate(
        val currency: String,
        val rate: Double
    )
}
